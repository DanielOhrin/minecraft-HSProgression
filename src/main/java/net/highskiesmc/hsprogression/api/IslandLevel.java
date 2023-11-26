package net.highskiesmc.hsprogression.api;

public class IslandLevel {
    private final int level;
    private final int maxSpawners;
    private final int maxMembers;
    private final int islandRadius;
    private final long cost;
    IslandLevel(int level, int maxSpawners, int maxMembers, int islandRadius, long cost) {
        this.level = level;
        this.maxSpawners = maxSpawners;
        this.maxMembers = maxMembers;
        this.islandRadius = islandRadius;
        this.cost = cost;
    }

}
