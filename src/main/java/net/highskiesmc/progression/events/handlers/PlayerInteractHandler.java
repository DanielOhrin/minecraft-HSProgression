package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.events.events.IslandProgressedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles claiming farming recipes
 */
public class PlayerInteractHandler implements Listener {
    private final HSProgressionAPI API;

    public PlayerInteractHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getHand().equals(EquipmentSlot.HAND)) {
            if (e.getItem().hasItemMeta()) {
                if (e.getItem().getItemMeta().getPersistentDataContainer().has(this.API.getRecipeCropTypeKey(),
                        PersistentDataType.STRING)) {
                    SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(e.getPlayer().getUniqueId());
                    Island island = superiorPlayer.getIsland();
                    ItemStack itemStack = e.getItem();
                    int itemStackAmount = e.getItem().getAmount();
                    String cropType =
                            itemStack.getItemMeta().getPersistentDataContainer().get(this.API.getRecipeCropTypeKey(),
                                    PersistentDataType.STRING);

                    if (island != null) {
                        if (!this.API.getIslandData(island.getUniqueId(), IslandDataType.FARMING, cropType).getBoolean("conditions-met")) {
                            if (itemStackAmount > 1) {
                                itemStack.setAmount(itemStackAmount - 1);
                            } else {
                                e.getPlayer().getInventory().remove(itemStack);
                                this.API.meetIslandDataConditions(island.getUniqueId(), IslandDataType.FARMING,
                                        cropType);
                                // Call island progressed event
                                IslandProgressedEvent event = new IslandProgressedEvent(island,
                                        IslandDataType.FARMING, cropType);
                                Bukkit.getPluginManager().callEvent(event);
                            }
                        } else {
                            e.getPlayer().sendMessage(ChatColor.RED + "Your island does not need this!");
                        }
                    } else {
                        e.getPlayer().sendMessage(ChatColor.RED + "You need an island to use this!");
                    }
                }
            }
        }
    }
}
