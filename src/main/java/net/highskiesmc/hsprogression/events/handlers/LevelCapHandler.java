package net.highskiesmc.hsprogression.events.handlers;

import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.api.Permission;
import net.highskiesmc.hsprogression.events.events.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;


import java.util.*;

public class LevelCapHandler extends HSListener {
    private static final Permission P = new Permission();
    private static final List<String> IRON_UNLOCKS = Arrays.asList(
        "SPIDER",
        "CREEPER",
        "SKELETON",
        "SLIME",
        "GUARDIAN",
        "ENDERMAN",
        "KELP",
        "NETHER_WART",
        "SWEET_BERRIES",
        "BAMBOO",
        "CHORUS_FLOWER",
        "iron",
        "gold",
        "quartz",
        "ruby",
        "PUFFERFISH",
        "highskiesmc:black_moor_goldfish",
        "highskiesmc:blue_parrotfish"
    );

    private static final List<String> DIAMOND_UNLOCKS = Arrays.asList(
            "MAGMA_CUBE",
            "BLAZE",
            "GHAST",
            "WITHER_SKELETON",
            "IRON_GOLEM",
            "CACTUS",
            "MELON_SLICE",
            "PUMPKIN",
            "COCOA_BEANS",
            "lapis",
            "diamond",
            "emerald",
            "debris",
            "highskiesmc:goldfish",
            "highskiesmc:green_sunfish",
            "highskiesmc:tuna"
    );

    public LevelCapHandler(HSPlugin main) {
        super(main);
    }

    private boolean isLocked(OfflinePlayer player, String key) {
            boolean hasDiamond = P.playerHas("", player, "hswarps.*") || P.playerHas("", player, "hswarps.adventure" +
                    ".unlocked.chainmail");

        // If player has diamond map
        if (hasDiamond) {
            return false;
        }

        if (DIAMOND_UNLOCKS.contains(key)) {
            return true;
        }

        // If thing is in iron
        if (IRON_UNLOCKS.contains(key)) {
            // If player has iron map
            return !P.playerHas("", player, "hswarps.adventure" +
                    ".unlocked.iron");
        }

        // If thing is chain
        return false;
    }

    @EventHandler
    public void onSlayerLevelUp(IslandSlayerLevelUpEvent e) {
        String key = e.getLevel().getEntity().name();

        if (isLocked(Bukkit.getOfflinePlayer(e.getIsland().getLeaderUuid()), key)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onFarmingLevelUp(IslandFarmingLevelUpEvent e) {
        String key = e.getLevel().getCrop().name();

        if (isLocked(Bukkit.getOfflinePlayer(e.getIsland().getLeaderUuid()), key)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onMiningLevelUp(IslandMiningLevelUpEvent e) {
        String key = e.getLevel().getNodeId();

        if (isLocked(Bukkit.getOfflinePlayer(e.getIsland().getLeaderUuid()), key)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onFishingLevelUp(IslandFishingLevelUpEvent e) {
        String key = e.getLevel().getId();

        if (isLocked(Bukkit.getOfflinePlayer(e.getIsland().getLeaderUuid()), key)) {
            e.setCancelled(true);
        }
    }
}
