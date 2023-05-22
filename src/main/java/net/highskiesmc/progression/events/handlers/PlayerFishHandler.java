package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import net.highskiesmc.progression.events.events.IslandFishCaughtEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerFishHandler implements Listener {
    @EventHandler
    public void onFishCaught(PlayerFishEvent e) {
        if (e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            Island island = SuperiorSkyblockAPI.getIslandAt(e.getHook().getLocation());
            Player player = e.getPlayer();

            if (island == null
                    || !island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()),
                    IslandPrivilege.getByName("FISH"))) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "[!] " +
                        ChatColor.RED + "You can only catch fish on an island where you have permission to!");
                e.getPlayer().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                return;
            }

            Item itemCaught = (Item) e.getCaught();

            ItemStack droppedItem = new ItemStack(Material.DIRT, 1);

            itemCaught.setItemStack(droppedItem);

            // Call the custom event
            IslandFishCaughtEvent event = new IslandFishCaughtEvent(player, itemCaught.getItemStack(),
                    e.getPlayer().getItemInUse(), island, e.getHook());
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }
}
