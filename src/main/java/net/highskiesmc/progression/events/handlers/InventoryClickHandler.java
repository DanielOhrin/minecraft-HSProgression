package net.highskiesmc.progression.events.handlers;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickHandler implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).equals(ChatColor.translateAlternateColorCodes('&', "&x&0&5&a&f&c&6&lI&x&0&c&b&6&c&b&ls&x&1&4&b&d&d&0&ll&x&1&b&c&4&d&5&la&x&2&3&c&b&d&a&ln&x&2&a&d&2&d&f&ld &x&3&2&d&a&e&4&lM&x&3&9&e&1&e&9&li&x&4&1&e&8&e&e&ln&x&4&8&e&f&f&3&li&x&5&0&f&6&f&8&ln&x&5&7&f&d&f&d&lg"))
                || ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).equals(ChatColor.translateAlternateColorCodes('&', "&x&0&5&a&f&c" +
                "&6&lI&x&0&c&b&6&c&b&ls&x&1&4&b&d&d&0&ll&x&1&b&c&4&d&5&la&x&2&3&c&b&d&a&ln&x&2&a&d&2&d&f&ld " +
                "&x&3&2&d&a&e&4&lS&x&3&9&e&1&e&9&ll&x&4&1&e&8&e&e&la&x&4&8&e&f&f&3&ly&x&5&0&f&6&f&8&le&x&5&7&f&d&f&d" +
                "&lr"))) {
            e.setCancelled(true);
        }
    }
}
