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
                "&lr"))
                || ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).equals(ChatColor.translateAlternateColorCodes('&', "&x&0&5&a&f&c&6&lI&x&0&c&b&6&c&b&ls&x&1&3&b&c&c&f&ll&x&1&a&c&3&d&4&la&x&2&0&c&9&d&8&ln&x&2&7&d&0&d&d&ld &x&2&e&d&6&e&2&lF&x&3&5&d&d&e&6&la&x&3&c&e&3&e&b&lr&x&4&3&e&a&e&f&lm&x&4&9&f&0&f&4&li&x&5&0&f&7&f&8&ln&x&5&7&f&d&f&d&lg"))) {
            e.setCancelled(true);
        }
    }
}
