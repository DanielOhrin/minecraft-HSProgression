package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.FarmingLevel;
import net.highskiesmc.hsprogression.api.FarmingRecipe;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.api.IslandProgressionType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            List<FarmingLevel> levels = api.getFarmingLevels();
            List<Material> seeds = levels.stream().map(FarmingLevel::getSeed).toList();
            // Make sure the crop is not the default one, and that it is tracked.
            if (material != levels.get(0).getSeed()) {
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
            List<FarmingLevel> levels = api.getFarmingLevels();
            List<Material> seeds = levels.stream().map(FarmingLevel::getSeed).toList();
            switch (crop) {
                case SUGAR_CANE,
                        CACTUS,
                        MELON,
                        PUMPKIN -> {
                    api.contributeFarming(null, island.getIslandUuid(), levels.get(seeds.indexOf(crop)).getCrop(), 1);
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

                    if (seeds.contains(crop)) {
                        api.contributeFarming(null, island.getIslandUuid(), levels.get(seeds.indexOf(crop)).getCrop(), 1);
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

    /**
     * Handles claiming Farming Recipe(s)
     *
     * @param e PlayerInteractEvent
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getHand().equals(EquipmentSlot.HAND)) {
            if (e.getItem() != null && !e.getItem().getType().equals(Material.AIR)) {
                if (e.getItem().hasItemMeta()) {
                    if (FarmingRecipe.isFarmingRecipe(e.getItem())) {
                        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(e.getPlayer().getUniqueId());
                        Island sIsland = superiorPlayer.getIsland();
                        ItemStack itemStack = e.getItem();
                        int itemStackAmount = e.getItem().getAmount();

                        if (sIsland != null) {
                            net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);
                            Material crop = FarmingRecipe.getCrop(itemStack);

                            if (island != null && island.canClaimRecipe(crop)) {
                                if (itemStackAmount > 1) {
                                    itemStack.setAmount(itemStackAmount - 1);
                                } else {
                                    e.getPlayer().getInventory().remove(itemStack);

                                    island.claimRecipe(superiorPlayer.asPlayer(), crop, config);
                                }
                            } else {
                                e.getPlayer().sendMessage(TextUtils.translateColor(
                                        config.get("island.not-unlocked", String.class, "&4&l[!]&c Island has not " +
                                                        "unlocked that! &cReason: &f{reason}")
                                                .replace("{reason}", "Recipe already claimed or first requirement not" +
                                                        " met")
                                ));
                            }
                        } else {
                            e.getPlayer().sendMessage(TextUtils.translateColor(
                                    config.get("common.no-island", String.class, "&c&lError | &7You don't have an " +
                                            "island.")
                            ));
                        }
                    }
                }
            }
        }
    }
}
