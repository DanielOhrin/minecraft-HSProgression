package net.highskiesmc.progression.inventories;

import com.mattisadev.mcore.inventory.GUI;
import net.highskiesmc.progression.api.IslandLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class IslandUpgradeGUI implements GUI {
    private static final String TITLE = ChatColor.translateAlternateColorCodes('&', "&x&0&8&4&c&f&bI&x&1&5&5&9&f" +
            "&bs&x&2&1&6&6&f&bl&x&2&e&7&3&f&ba&x&3&b&7&f&f&cn&x&4&7&8&c&f&cd " +
            "&x&5&4&9&9&f&cU&x&6&1&a&6&f&cp&x&6&e&b&3&f&cg&x&7&a&c&0&f&cr&x&8&7&c&c&f&da&x&9&4&d&9&f&dd&x&a&0&e&6&f" +
            "&de&x&a&d&f&3&f&ds");

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public void onInventoryOpen(InventoryOpenEvent e) {

    }

    @Override
    public void onInventoryClose(InventoryCloseEvent e) {

    }

    @Override
    public void addContent(Inventory inv) {
        inv.clear();

        // TODO: Implement each level's item
    }

    private void getLevelItem(IslandLevel level) {
        // TODO: Make somewhat generic so its not just a wall of code...
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, TITLE);

        addContent(inv);

        return inv;
    }
}