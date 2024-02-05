package net.highskiesmc.hsprogression.api;

import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Island {
    private final Integer id;
    private UUID leaderUuid;
    private final UUID islandUuid;
    private final Map<IslandProgressionType, Integer> levels;
    private final Map<EntityType, Integer> slayer;
    private boolean isDeleted;

    Island(int id, @NonNull UUID leaderUuid, @NonNull UUID islandUuid, int level, int slayerLevel, boolean isDeleted) {
        this.id = id;
        this.leaderUuid = leaderUuid;
        this.islandUuid = islandUuid;
        this.levels = new HashMap<>(){{
            put(IslandProgressionType.ISLAND, level);
            put(IslandProgressionType.SLAYER, slayerLevel);
        }};

        this.slayer = new HashMap<>();
        this.isDeleted = isDeleted;
    }

    Island(@NonNull UUID leaderUuid, @NonNull UUID islandUuid, int level, int slayerLevel, boolean isDeleted) {
        this.id = null;
        this.leaderUuid = leaderUuid;
        this.islandUuid = islandUuid;
        this.levels = new HashMap<>(){{
            put(IslandProgressionType.ISLAND, level);
            put(IslandProgressionType.SLAYER, slayerLevel);
        }};

        this.slayer = new HashMap<>();
        this.isDeleted = isDeleted;
    }

    @Nullable
    Integer getId() {
        return id;
    }

    public UUID getIslandUuid() {
        return islandUuid;
    }

    public int getLevel(@NonNull IslandProgressionType type) {
        return levels.get(type);
    }

    public UUID getLeaderUuid() {
        return leaderUuid;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void setLeaderUuid(UUID leaderUuid) {
        this.leaderUuid = leaderUuid;
    }

    public void setLevel(IslandProgressionType type, int newLevel) {
        this.levels.put(type, newLevel);
    }

    public int getSlayerAmount(EntityType type) {
        return slayer.getOrDefault(type, 0);
    }
    public void contributeSlayer(EntityType type, int amount) {
        if (!this.slayer.containsKey(type)) {
            this.slayer.put(type, 0);
        }
        this.slayer.put(type, this.slayer.get(type) + amount);
        // TODO: Check if slayer leveled up
    }
}
