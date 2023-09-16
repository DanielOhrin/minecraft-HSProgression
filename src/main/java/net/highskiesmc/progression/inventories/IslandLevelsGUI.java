package net.highskiesmc.progression.inventories;

import com.mattisadev.mcore.inventory.GUI;
import net.highskiesmc.progression.api.IslandLevel;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class IslandLevelsGUI implements GUI {
    private final static int MAX_LEVEL = IslandLevel.values().length;
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

    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
