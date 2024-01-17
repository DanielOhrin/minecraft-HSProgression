package net.highskiesmc.hsprogression.api;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class HSProgressionApi {
    //<editor-fold desc="Structure">
    //<editor-fold desc="Fields">
    private final HSProgression main;
    private List<IslandLevel> islandLevels;
    private List<SlayerLevel> slayerLevels;
    private Map<Integer, List<IslandBlock>> islandBlocks;
    private Map<UUID, Island> islands;
    private Database db;
    private final int taskId;

    //</editor-fold>
    //<editor-fold desc="Constructor">
    public HSProgressionApi(@NonNull HSProgression main, @NonNull ConfigurationSection dbConfig) throws SQLException,
            IOException {
        this.main = main;
        db = new Database(main, dbConfig);

        // Populate with data
        this.islandLevels = db.getIslandLevels();
        this.slayerLevels = db.getSlayerLevels();
        this.islandBlocks = db.getIslandBlocks();
        this.islands = db.getIslands();

        long cachePushInterval = Long.parseLong(main.getConfigs().get("cache.upload.interval.seconds", String.class,
                "300"));
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
        this.uploadCacheToDatabase();
        this.db.disconnect();
        this.islands = null;
        this.islandLevels = null;
        this.slayerLevels = null;
        this.islandBlocks = null;
        this.db = null;
    }

    /**
     * Uploads cache to DB (Async)
     */
    private void uploadCacheToDatabaseAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, this::uploadCacheToDatabase);
    }

    /**
     * Uploads cache to DB (Sync)
     */
    private void uploadCacheToDatabase() {
        db.upsertIslands(this.main, islands.values().stream().toList());
    }

    //</editor-fold>
    //</editor-fold>
    //<editor-fold desc="Getters">
    @NonNull
    public List<IslandLevel> getIslandLevels() {
        return Collections.unmodifiableList(this.islandLevels);
    }

    @NonNull
    public IslandLevel getIslandLevel(int level) throws IndexOutOfBoundsException {
        return this.islandLevels.get(level - 1);
    }

    @NonNull
    public List<SlayerLevel> getSlayerLevels() {
        return Collections.unmodifiableList(this.slayerLevels);
    }

    @NonNull
    public SlayerLevel getSlayerLevel(int level) throws IndexOutOfBoundsException {
        return this.slayerLevels.get(level - 1);
    }

    @NonNull
    public List<IslandBlock> getIslandBlocks(int level) {
        return Collections.unmodifiableList(this.islandBlocks.getOrDefault(level, new ArrayList<>()));
    }
    //</editor-fold>

    //<editor-fold desc="Data Handling">

    //<editor-fold desc="Island">
    //<editor-fold desc="Create">
    private void createIsland(@NonNull UUID islandUuid, @NonNull UUID leaderUuid, int level,
                              int slayerLevel, boolean isDeleted) {
        this.islands.put(islandUuid, new Island(leaderUuid, islandUuid, level, slayerLevel, isDeleted));
    }

    /**
     * Creates an island in HSProgression data. Default level is 1 and isDeleted is false
     *
     * @param island Source island
     */
    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), 1, 1, false);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, boolean isDeleted) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), 1, 1, isDeleted);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, int level, int slayerLevel) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), level, slayerLevel, false);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, int level,
                             int slayerLevel, boolean isDeleted) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), level, slayerLevel, isDeleted);
    }

    //</editor-fold>
    //<editor-fold desc="Read">
    public Island getIsland(UUID islandUuid) throws NullPointerException {
        return islands.get(islandUuid);
    }

    public Island getIsland(com.bgsoftware.superiorskyblock.api.island.Island island) {
        return getIsland(island.getUniqueId());
    }

    //</editor-fold>
    //<editor-fold desc="Update">
    public void setIslandLevel(Island island, int level) throws NullPointerException {
        islands.get(island.getIslandUuid()).setLevel(IslandProgressionType.ISLAND, level);
    }

    public void setIslandLevel(UUID islandUuid, int level) throws NullPointerException {
        islands.get(islandUuid).setLevel(IslandProgressionType.ISLAND, level);
    }

    public void setIslandLevel(com.bgsoftware.superiorskyblock.api.island.Island island, int level) throws NullPointerException {
        setIslandLevel(island.getUniqueId(), level);
    }

    public void setIslandLeader(UUID islandUuid, UUID leaderUuid) throws NullPointerException {
        islands.get(islandUuid).setLeaderUuid(leaderUuid);
    }

    public void setIslandLeader(com.bgsoftware.superiorskyblock.api.island.Island island, Player leader) throws NullPointerException {
        setIslandLeader(island.getUniqueId(), leader.getUniqueId());
    }

    public void setIslandLeader(com.bgsoftware.superiorskyblock.api.island.Island island, SuperiorPlayer leader) throws NullPointerException {
        setIslandLeader(island.getUniqueId(), leader.asOfflinePlayer().getUniqueId());
    }

    //</editor-fold>
    //<editor-fold desc="Delete">

    /**
     * Soft-deletes an island
     *
     * @param island Source island
     * @throws NullPointerException If island does not exist in the cache
     */
    public void deleteIsland(com.bgsoftware.superiorskyblock.api.island.Island island) throws NullPointerException {
        this.islands.get(island.getUniqueId()).delete();
    }

    /**
     * Soft-deletes an island
     *
     * @param island Source island
     * @throws NullPointerException If island does not exist in the cache
     */
    public void deleteIsland(@NonNull Island island) throws NullPointerException {
        this.islands.get(island.getIslandUuid()).delete();
    }
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>
    //<editor-fold desc="Util">

    /**
     * Checks IslandLevelBlocks to see if the block is unlocked. Be careful, as this does NOT check max allowed placed.
     *
     * @param island Island to check
     * @param item   Item being placed
     * @return Whether the item can be placed, or default to TRUE if not restricted in the first place
     */
    public boolean canPlace(Island island, ItemStack item) {
        int level = island.getLevel(IslandProgressionType.ISLAND);

        for (int i = 0; i < islandBlocks.size(); i++) {
            List<IslandBlock> blocks = getIslandBlocks(i + 1);

            for (IslandBlock block : blocks) {
                if (block.getItem().isSimilar(item)) {
                    return (i + 1) <= level;
                }
            }
        }

        return true;
    }
    //</editor-fold>
}
