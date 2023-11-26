package net.highskiesmc.hsprogression.api;

import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IslandBlock {
    private final int islandLevel;
    private final ItemStack item;
    private final String label;
    IslandBlock(int islandLevel, @NonNull ItemStack item, @NonNull String label) {
        this.islandLevel = islandLevel;
        this.item = item;
        this.label = label;
    }
    public int getIslandLevel() {
        return islandLevel;
    }

    public ItemStack getItem() {
        return item;
    }
}
