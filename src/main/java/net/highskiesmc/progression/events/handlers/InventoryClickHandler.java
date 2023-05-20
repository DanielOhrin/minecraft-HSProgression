package net.highskiesmc.progression.events.handlers;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickHandler implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).equals(ChatColor.DARK_PURPLE + "Island Mining")) {
            e.setCancelled(true);
        }
    }
}
