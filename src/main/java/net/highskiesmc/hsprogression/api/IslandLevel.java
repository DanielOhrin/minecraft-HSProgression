package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hsprogression.HSProgression;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IslandLevel {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private static final Map<Boolean, Material> ITEM =
            new HashMap<>() {{
                put(true, Material.LIME_STAINED_GLASS);
                put(false, Material.RED_STAINED_GLASS);
            }};
    private final int level;
    private final int maxSpawners;
    private final int maxMembers;
    private final int islandRadius;
    private final long cost;
    private final boolean isAnnounced;
    IslandLevel(int level, int maxSpawners, int maxMembers, int islandRadius, long cost, boolean isAnnounced) {
        this.level = level;
        this.maxSpawners = maxSpawners;
        this.maxMembers = maxMembers;
        this.islandRadius = islandRadius;
        this.cost = cost;
        this.isAnnounced = isAnnounced;
    }
    //<editor-fold desc="Getters">
    public int getLevel() {
        return level;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public int getIslandRadius() {
        return islandRadius;
    }

    public int getMaxSpawners() {
        return maxSpawners;
    }

    public long getCost() {
        return cost;
    }

    public boolean isAnnounced() {
        return isAnnounced;
    }

    //</editor-fold>
    @NonNull
    public ItemStack toDisplayItem(double balance, int islandLevel) {
        boolean isUnlocked = islandLevel >= level;
        String color = (isUnlocked ? ChatColor.GREEN : ChatColor.RED).toString() + ChatColor.BOLD;

        ItemStack item = new ItemStack(ITEM.get(isUnlocked));

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(color + "Level " + level);

        String cost = level == 1 ? ChatColor.WHITE + " FREE" :
                ChatColor.WHITE.toString() + ChatColor.BOLD + " $" + FORMATTER.format(this.cost);
        String prefix = ChatColor.GREEN.toString() + ChatColor.BOLD + " * ";
        LinkedList<String> lore = new LinkedList<>() {{
            add("");
            add(ChatColor.AQUA.toString() + ChatColor.BOLD + "Cost");
            add(cost);
            add("");
            add(ChatColor.GREEN.toString() + ChatColor.BOLD + "Level Rewards");
            add(prefix + ChatColor.WHITE + "Spawner Limit: " + FORMATTER.format(maxSpawners));
            add(prefix + ChatColor.WHITE + "Island Radius: " + FORMATTER.format(islandRadius));
        }};

        List<IslandBlock> blocks = HSProgression.getApi().getIslandBlocks(level);
        for (String label : blocks.stream().map(IslandBlock::getLabel).collect(Collectors.toSet())) {
            lore.add(prefix + ChatColor.WHITE + label);
        }

        if (level != 1) {
            int memberDifference = maxMembers - HSProgression.getApi().getIslandLevel(level - 1).getMaxMembers();
            if (memberDifference > 0) {
                lore.add(prefix + ChatColor.WHITE + "+" + memberDifference + " Max Members (" + maxMembers + ")");
            }
        }

        lore.add("");
        boolean isCurrent = islandLevel == level;
        lore.add(color + (isUnlocked ? isCurrent ? "CURRENT LEVEL" : "UNLOCKED" : "LOCKED"));

        if (level - islandLevel > 1) {
            lore.add(ChatColor.RED + "Requires Island Level " + ChatColor.UNDERLINE + (level - 1) + ChatColor.RED +
                    "!");
        } else if (balance < this.cost) {
            lore.add(ChatColor.RED + "You cannot afford this level up!");
        } else if (level - 1 == islandLevel) {
            lore.add(ChatColor.GRAY + "Click to level up!");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
