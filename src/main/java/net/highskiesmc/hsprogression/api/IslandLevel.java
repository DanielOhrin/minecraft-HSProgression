package net.highskiesmc.hsprogression.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

// TODO Change into a class
public enum IslandLevel {
    // Instances
    L1(10, 16, 4, 0L),
    L2(25, 20, 5, 2500L),
    L3(50, 25, 5, 25000L),
    L4(75, 29, 6, 50000L),
    L5(100, 34, 7, 250000L),
    L6(150, 38, 7, 750000L),
    L7(250, 43, 8, 1500000L),
    L8(500, 47, 8, 5000000L),
    L9(750, 52, 9, 20000000L),
    L10(1000, 54, 9, 50000000L),
    L11(1250, 61, 10, 100000000L),
    L12(1500, 65, 10, 200000000L),
    L13(1750, 70, 10, 250000000L),
    L14(2000, 75, 11, 500000000L),
    L15(2500, 79, 11, 1000000000L),
    L16(3000, 83, 12, 1500000000L),
    L17(4000, 88, 12, 2500000000L),
    L18(5000, 92, 13, 5000000000L);

    // Public fields
    public final int MAX_SPAWNERS;
    public final int ISLAND_RADIUS;
    public final int MAX_MEMBERS;
    public final long COST;
    public static final int MAX = 18;

    // Constructor
    IslandLevel(int maxSpawners, int islandRadius, int maxMembers, long cost) {
        this.MAX_SPAWNERS = maxSpawners;
        this.ISLAND_RADIUS = islandRadius;
        this.MAX_MEMBERS = maxMembers;
        this.COST = cost;
    }

    // Public methods

    /**
     * @param islandLevel Island's current level
     * @return ItemMeta for the item(s)
     */
    public ItemStack getItem(int islandLevel) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        int itemLevel = ordinal() + 1;
        boolean isUnlocked = islandLevel >= itemLevel;
        String color = (isUnlocked ? ChatColor.GREEN : ChatColor.RED).toString() + ChatColor.BOLD;

        ItemStack item = new ItemStack(ITEM.get(isUnlocked));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(color + "Level " + itemLevel);

        String cost = itemLevel == 1 ? ChatColor.WHITE + " FREE" :
                ChatColor.WHITE.toString() + ChatColor.BOLD + " $" + formatter.format(this.COST);
        String prefix = ChatColor.GREEN.toString() + ChatColor.BOLD + " * ";
        LinkedList<String> lore = new LinkedList<>()
        {{
            add("");
            add(ChatColor.AQUA.toString() + ChatColor.BOLD + "Cost");
            add(cost);
            add("");
            add(ChatColor.GREEN.toString() + ChatColor.BOLD + "Level Rewards");
            add(prefix + ChatColor.WHITE + "Spawner Limit: " + formatter.format(MAX_SPAWNERS));
            add(prefix + ChatColor.WHITE + "Island Radius: " + formatter.format(ISLAND_RADIUS));
        }};

        for(String line : LEVEL_REWARDS[itemLevel - 1])
        {
            lore.add(prefix + ChatColor.WHITE + line);
        }

        if (itemLevel != 1) {
            int memberDifference = MAX_MEMBERS - IslandLevel.values()[ordinal() - 1].MAX_MEMBERS;
            if (memberDifference > 0) {
                lore.add(prefix + ChatColor.WHITE + "+" + memberDifference + " Max Members (" + MAX_MEMBERS + ")");
            }
        }
// TODO: Possibly enchant unlocked items or the next unlockable one
        lore.add("");
        lore.add(color + (isUnlocked ? "UNLOCKED" : "LOCKED"));

        // TODO: Check if player can afford the upgrade and add lore if not

        if (itemLevel - islandLevel > 1) {
            lore.add(ChatColor.RED + "Requires Island Level " + ChatColor.UNDERLINE + (itemLevel - 1) + ChatColor.RED + "!");
        }


        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Private fields
    private static final Map<Boolean, Material> ITEM =
            new HashMap<>() {{
                put(true, Material.LIME_STAINED_GLASS);
                put(false, Material.RED_STAINED_GLASS);
            }};
    private static final String[][] LEVEL_REWARDS = new String[][]
            {
                    new String[]{},
                    new String[]{"Ability to place Furnaces"},
                    new String[]{"Ability to place Redstone"},
                    new String[]{},
                    new String[]{"Ability to place Hoppers"},
                    new String[]{"Ability to place Anvils"},
                    new String[]{"Ability to place Enchant Tables"},
                    new String[]{"Ability to place Observers"},
                    new String[]{},
                    new String[]{"Ability to place Pistons"},
                    new String[]{},
                    new String[]{},
                    new String[]{},
                    new String[]{},
                    new String[]{},
                    new String[]{},
                    new String[]{},
                    new String[]{},
                    new String[]{},
            };
}

//public enum IslandLevel {
//    1;
//    IslandLevel()
//    {
//
//    }
//    // Public static fields
//    public static final int MAX = 18;
//
//    // Private static fields
//    private static final int[] SPAWNER_LIMITS = new int[]
//            {
//                    10,
//                    25,
//                    50,
//                    75,
//                    100,
//                    150,
//                    250,
//                    500,
//                    750,
//                    1000,
//                    1250,
//                    1500,
//                    1750,
//                    2000,
//                    2500,
//                    3000,
//                    4000,
//                    5000
//            };
//
//    // Private fields
//    private int level;
//    private int spawnerLimit = SPAWNER_LIMITS[this.level];
//    private int islandRadius;
//    private int maxMembers;
//
//    // Constructor
//    private IslandLevel(@IntRange(from = 1, to = MAX) int level) {
//        this.level = level;
//    }
//
//    public static IslandLevel of(@IntRange(from = 1, to = MAX) int level) {
//        return new IslandLevel(level);
//    }
//
//    // Public methods
//    public int getSpawnerLimit()
//    {
//        return this.spawnerLimit;
//    }
//    public int getLevel()
//    {
//        return this.level;
//    }
//
//    public int getMaxMembers()
//    {
//        return this.maxMembers;
//    }
//
//    public int getIslandRadius()
//    {
//        return this.islandRadius;
//    }
//
//    public boolean isPlaceable(Material material) {
//        // Check for blocks restricted by *island level*
//        switch (material) {
//            case FURNACE -> {
//                return this.level != 1; // Level 2+
//            }
//            case REDSTONE,
//                    REDSTONE_TORCH,
//                    LEVER,
//                    ACACIA_BUTTON,
//                    BAMBOO_BUTTON,
//                    BIRCH_BUTTON,
//                    CHERRY_BUTTON,
//                    CRIMSON_BUTTON,
//                    DARK_OAK_BUTTON,
//                    JUNGLE_BUTTON,
//                    MANGROVE_BUTTON,
//                    OAK_BUTTON,
//                    POLISHED_BLACKSTONE_BUTTON,
//                    SPRUCE_BUTTON,
//                    STONE_BUTTON,
//                    WARPED_BUTTON,
//                    POLISHED_BLACKSTONE_PRESSURE_PLATE,
//                    ACACIA_PRESSURE_PLATE,
//                    BAMBOO_PRESSURE_PLATE,
//                    BIRCH_PRESSURE_PLATE,
//                    CHERRY_PRESSURE_PLATE,
//                    CRIMSON_PRESSURE_PLATE,
//                    DARK_OAK_PRESSURE_PLATE,
//                    HEAVY_WEIGHTED_PRESSURE_PLATE,
//                    JUNGLE_PRESSURE_PLATE,
//                    LIGHT_WEIGHTED_PRESSURE_PLATE,
//                    MANGROVE_PRESSURE_PLATE,
//                    OAK_PRESSURE_PLATE,
//                    SPRUCE_PRESSURE_PLATE,
//                    STONE_PRESSURE_PLATE,
//                    WARPED_PRESSURE_PLATE,
//                    REDSTONE_BLOCK,
//                    DAYLIGHT_DETECTOR,
//                    TRIPWIRE_HOOK,
//                    TRIPWIRE -> {
//                return this.level >= 3; // Level 3+
//            }
//            case HOPPER -> {
//                return this.level >= 5; // Level 5+
//            }
//            case ANVIL,
//                    CHIPPED_ANVIL,
//                    DAMAGED_ANVIL -> {
//                return this.level >= 6; // Level 6+
//            }
//            case ENCHANTING_TABLE -> {
//                return this.level >= 7; // Level 7+
//            }
//            case OBSERVER -> {
//                return this.level >= 8; // Level 8+
//            }
//            case PISTON,
//                    STICKY_PISTON -> {
//                return this.level >= 10; // Level 10+
//            }
//        }
//
//        return true;
//    }
//
//}
