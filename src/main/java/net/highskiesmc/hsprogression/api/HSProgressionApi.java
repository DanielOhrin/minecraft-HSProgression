package net.highskiesmc.hsprogression.api;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class HSProgressionApi {
    //<editor-fold desc="Structure">
    //<editor-fold desc="Fields">
    private final HSProgression main;
    private List<IslandLevel> islandLevels;
    private List<SlayerLevel> slayerLevels;
    private List<FarmingLevel> farmingLevels;
    private List<MiningLevel> miningLevels;
    private Map<Integer, List<IslandBlock>> islandBlocks;
    private Map<UUID, Island> islands;
    private Database db;
    private final int taskId;
    private boolean useFirstCache = true;
    private final Map<Boolean, Map<UUID, IslandContributor>> islandContributors;

    //</editor-fold>
    //<editor-fold desc="Constructor">
    public HSProgressionApi(@NonNull HSProgression main, @NonNull ConfigurationSection dbConfig) throws SQLException,
            IOException {
        this.main = main;
        db = new Database(main, dbConfig);
        this.islandContributors = new HashMap<>() {{
            put(true, new HashMap<>());
            put(false, new HashMap<>());
        }};

        // Populate with data
        this.islandLevels = db.getIslandLevels();
        this.slayerLevels = db.getSlayerLevels();
        this.farmingLevels = db.getFarmingLevels();
        this.miningLevels = db.getMiningLevels();
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
        this.farmingLevels = null;
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
        Map<UUID, IslandContributor> cache = new HashMap<>(getCache(true));
        islandContributors.get(!useFirstCache).clear();

        db.upsertIslands(this.main, islands.values().stream().toList());
        db.upsertContributions(this.main, cache);
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

    @Nullable
    public List<DisplayableItem> getLevelItems(IslandProgressionType type) {
        List<DisplayableItem> result;

        switch (type) {
            case SLAYER -> result = new ArrayList<>(getSlayerLevels());
            case FARMING -> result = new ArrayList<>(getFarmingLevels());
            case MINING -> result = new ArrayList<>(getMiningLevels());
            default -> result = null;
        }

        return result;
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
    public List<FarmingLevel> getFarmingLevels() {
        return Collections.unmodifiableList(this.farmingLevels);
    }

    @NonNull
    public FarmingLevel getFarmingLevel(int level) throws IndexOutOfBoundsException {
        return this.farmingLevels.get(level - 1);
    }

    @NonNull
    public List<MiningLevel> getMiningLevels() {
        return miningLevels;
    }

    @NonNull
    public MiningLevel getMiningLevel(int level) throws IndexOutOfBoundsException {
        return this.miningLevels.get(level - 1);
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
                              int slayerLevel, int farmingLevel, int miningLevel, boolean isDeleted) {
        this.islands.put(islandUuid, new Island(leaderUuid, islandUuid, level, slayerLevel, farmingLevel,
                miningLevel, isDeleted));
    }

    /**
     * Creates an island in HSProgression data. Default level is 1 and isDeleted is false
     *
     * @param island Source island
     */
    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), 1, 1, 1, 1, false);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, boolean isDeleted) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), 1, 1, 1, 1, isDeleted);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, int level, int slayerLevel,
                             int farmingLevel, int miningLevel) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), level, slayerLevel, farmingLevel,
                miningLevel, false);
    }

    public void createIsland(com.bgsoftware.superiorskyblock.api.island.Island island, int level,
                             int slayerLevel, int farmingLevel, int miningLevel, boolean isDeleted) {
        createIsland(island.getUniqueId(), island.getOwner().getUniqueId(), level, slayerLevel, farmingLevel,
                miningLevel, isDeleted);
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

    //<editor-fold desc="Skills">
    public void contributeSlayer(UUID playerUuid, UUID islandUuid, EntityType entity, int amount) {
        Map<UUID, IslandContributor> contributors = islandContributors.get(useFirstCache);

        if (!contributors.containsKey(playerUuid)) {
            contributors.put(playerUuid, new IslandContributor(playerUuid));
        }

        IslandContributor contributor = contributors.get(playerUuid);

        contributor.addSlayerContribution(islandUuid, entity, amount);
        this.islands.get(islandUuid).contributeSlayer(entity, amount, main.getConfigs());
    }
    public void contributeFarming(UUID playerUuid, UUID islandUuid, Material crop, int amount) {
        Map<UUID, IslandContributor> contributors = islandContributors.get(useFirstCache);

        if (!contributors.containsKey(playerUuid)) {
            contributors.put(playerUuid, new IslandContributor(playerUuid));
        }

        IslandContributor contributor = contributors.get(playerUuid);

        contributor.addFarmingContribution(islandUuid, crop, amount);
        this.islands.get(islandUuid).contributeFarming(crop, amount, main.getConfigs());
    }

    public void contributeMining(UUID playerUuid, UUID islandUuid, String nodeId, int amount) {
        Map<UUID, IslandContributor> contributors = islandContributors.get(useFirstCache);

        if (!contributors.containsKey(playerUuid)) {
            contributors.put(playerUuid, new IslandContributor(playerUuid));
        }

        IslandContributor contributor = contributors.get(playerUuid);

        contributor.addMiningContribution(islandUuid, nodeId, amount);
        this.islands.get(islandUuid).contributeMining(nodeId, amount, main.getConfigs());
    }

    public Map<UUID, IslandContributor> getCache(boolean swapCache) {
        if (swapCache) {
            this.useFirstCache = !this.useFirstCache;
        }

        return islandContributors.get(!this.useFirstCache);
    }
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
