package net.highskiesmc.hsprogression.api;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public class Island {
    private final int id;
    private UUID leaderUuid;
    private final UUID islandUuid;
    private int level;
    private boolean isDeleted;

    Island(int id, @NonNull UUID leaderUuid, @NonNull UUID islandUuid, int level, boolean isDeleted) {
        this.id = id;
        this.leaderUuid = leaderUuid;
        this.islandUuid = islandUuid;
        this.level = level;
        this.isDeleted = isDeleted;
    }

    int getId() {
        return id;
    }

    public UUID getIslandUuid() {
        return islandUuid;
    }

    public int getLevel() {
        return level;
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

    public void setLevel(int newLevel) {
        this.level = newLevel;
    }
}
