package net.highskiesmc.hsprogression.api;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Island {
    private final Integer id;
    private UUID leaderUuid;
    private final UUID islandUuid;
    private Map<IslandProgressionType, Integer> levels;
    private boolean isDeleted;

    Island(int id, @NonNull UUID leaderUuid, @NonNull UUID islandUuid, int level, int slayerLevel, boolean isDeleted) {
        this.id = id;
        this.leaderUuid = leaderUuid;
        this.islandUuid = islandUuid;
        this.levels = new HashMap<>(){{
            put(IslandProgressionType.ISLAND, level);
            put(IslandProgressionType.SLAYER, slayerLevel);
        }};
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
}
