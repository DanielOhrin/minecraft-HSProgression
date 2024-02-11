package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.FarmingLevel;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.api.IslandProgressionType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;


import java.util.List;

public class IslandFarmingEventsHandler extends HSListener {
    private final HSProgressionApi api;
    public IslandFarmingEventsHandler(HSPlugin main, HSProgressionApi api) {
        super(main);
        this.api = api;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        // Check if was on island and player has permission
        Player player = e.getPlayer();
        Island sIsland = SuperiorSkyblockAPI.getIslandAt(e.getBlockPlaced().getLocation());

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island != null) {
            Material material = e.getBlockPlaced().getType();
            List<Material> seeds =
                    api.getFarmingLevels().stream().map(FarmingLevel::getSeed).toList();
            // Make sure the crop is not the default one, and that it is tracked.
            if (material != seeds.get(0)) {
                if (seeds.contains(material)) {
                    if (island.getLevel(IslandProgressionType.FARMING) < (seeds.indexOf(material) + 1)) {
                        e.setCancelled(true);
                        Sound sound = Sound.valueOf(config.get("gui.locked.sound", String.class, "BLOCK_ANVIL_PLACE"));
                        String msg = TextUtils.translateColor(
                                config.get("island.not-unlocked", String.class, "&4&l[!]&c Island has not unlocked " +
                                                "that! &cReason: &f{reason}")
                                        .replace("{reason}", "Insufficient farming level")
                        );

                        player.sendMessage(msg);
                        player.playSound(player.getLocation(), sound, 1, 1);
                    }

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent e) {
        Island sIsland = SuperiorSkyblockAPI.getIslandAt(e.getBlock().getLocation());

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island != null) {
            // Handle edge cases
            // Note: Although this hard-coding is not ideal, it is very easy to edit/remove still
            Material crop = e.getNewState().getType();
            switch (crop) {
                case SUGAR_CANE,
                        CACTUS,
                        MELON,
                        PUMPKIN -> {
                    api.contributeFarming(null, island.getIslandUuid(), crop, 1);
                    return;
                }
                case PUMPKIN_STEM,
                        MELON_STEM -> {
                    return;
                }
                default -> {
                    break;
                }
            }

            if (e.getNewState().getBlockData() instanceof Ageable ageData) {
                // If crop is fully grown

                if (ageData.getAge() == ageData.getMaximumAge()) {
                    // Now check if the block is being tracked, and increment it if so.
                    List<Material> crops =
                            api.getFarmingLevels().stream().map(FarmingLevel::getCrop).toList();

                    if (crops.contains(crop)) {
                        api.contributeFarming(null, island.getIslandUuid(), crop, 1);
                    }
                }
            }
        }
    }
// TODO: Track players BREAKING crops
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockSpread(BlockSpreadEvent e) {
        // Handle kelp/bamboo (edge cases)
        Island sIsland = SuperiorSkyblockAPI.getIslandAt(e.getBlock().getLocation());

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island != null) {
            switch (e.getSource().getType()) {
                case BAMBOO:
                case BAMBOO_SAPLING:
                    api.contributeFarming(null, island.getIslandUuid(), Material.BAMBOO, 1);
                    break;
                case KELP:
                    api.contributeFarming(null, island.getIslandUuid(), Material.KELP, 1);
                default:
                    break;
            }
        }
    }
}
