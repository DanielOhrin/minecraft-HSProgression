package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.value.qual.IntRange;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HSProgressionApi {
    //<editor-fold desc="Structure">
    //<editor-fold desc="Fields">
    private final HSProgression main;
    private List<IslandLevel> islandLevels;
    private final int maxIslandLevel;
    private Map<Integer, List<IslandBlock>> islandBlocks;
    private Map<UUID, Island> islands;
    private Database db;
    private final int taskId;

    //</editor-fold>
    //<editor-fold desc="Constructor">
    public HSProgressionApi(@NonNull HSProgression main, @NonNull ConfigurationSection dbConfig) throws SQLException,
            IOException {
        this.main = main;
        db = new Database(dbConfig);

        // Populate with data
        this.islandLevels = db.getIslandLevels();
        this.maxIslandLevel = islandLevels.size();
        this.islandBlocks = db.getIslandBlocks();
        this.islands = db.getIslands();

        // TODO: extract to HSCore
        // TODO: Add info logging to it.
        long cachePushInterval;
        try (InputStream stream = HSProgression.class.getResourceAsStream("/config.xml")) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(stream);
            doc.getDocumentElement().normalize();

            NodeList values = doc.getElementsByTagName("value");

            boolean matchFound = false;
            Node node = null;
            for (int i = 0; i < values.getLength(); i++) {
                node = values.item(i);

                if (node.getAttributes().getNamedItem("key").getNodeValue().equalsIgnoreCase("cache.upload.interval" +
                        ".seconds")) {
                    matchFound = true;
                    break;
                }
            }

            if (!matchFound) {
                throw new IOException("cache.upload.interval.seconds not found in config.xml");
            }

            cachePushInterval = Long.parseLong(node.getTextContent()) * 20L;
        } catch (SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }

        this.taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this.main,
                this::uploadCacheToDatabaseAsync,
                cachePushInterval,
                cachePushInterval
        ).getTaskId();
    }
//</editor-fold>
    //<editor-fold desc="Methods">

    /**
     * Cleans memory usage by removing all data and disconnecting from DB
     */
    public void dispose() {
        Bukkit.getScheduler().cancelTask(this.taskId);
        this.uploadCacheToDatabaseAsync();
        this.db.disconnect();
        this.islands = null;
        this.islandLevels = null;
        this.islandBlocks = null;
        this.db = null;
    }

    private void uploadCacheToDatabaseAsync() {
        db.upsertIslandsAsync(this.main, islands.values().stream().toList());
    }

    //</editor-fold>
    //</editor-fold>
    //<editor-fold desc="Getters">
    @NonNull
    public List<IslandLevel> getIslandLevels() {
        return this.islandLevels;
    }

    @NonNull
    public IslandLevel getIslandLevel(int level) throws IndexOutOfBoundsException {
        return this.islandLevels.get(level - 1);
    }

    @NonNull
    public List<IslandBlock> getIslandBlocks(int level) {
        return this.islandBlocks.getOrDefault(level, new ArrayList<>());
    }
    //</editor-fold>

    //<editor-fold desc="Data Handling">

    //<editor-fold desc="Island">
    //<editor-fold desc="Create">
    private void createIsland(@NonNull UUID islandUuid, @NonNull UUID leaderUuid, int level, boolean isDeleted) {
        this.islands.put(islandUuid, new Island(leaderUuid, islandUuid, level, isDeleted));
    }

    /**
     * Creates an island in HSProgression data. Default level is 1 and isDeleted is false
     * @param island Source island
     */
    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), 1, false);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, boolean isDeleted) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), 1, isDeleted);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, int level) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), level, false);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, int level, boolean isDeleted) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), level, isDeleted);
    }
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>
}
