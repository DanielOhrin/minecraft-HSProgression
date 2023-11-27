package net.highskiesmc.hsprogression.inventories;

import net.highskiesmc.hscore.inventory.GUI;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.IslandLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.List;

public class IslandUpgradeGUI implements GUI {
    // TODO: Make this a config.yml value
    private static final String TITLE = ChatColor.translateAlternateColorCodes('&', "&x&0&8&4&c&f&bI&x&1&5&5&9&f" +
            "&bs&x&2&1&6&6&f&bl&x&2&e&7&3&f&ba&x&3&b&7&f&f&cn&x&4&7&8&c&f&cd " +
            "&x&5&4&9&9&f&cU&x&6&1&a&6&f&cp&x&6&e&b&3&f&cg&x&7&a&c&0&f&cr&x&8&7&c&c&f&da&x&9&4&d&9&f&dd&x&a&0&e&6&f" +
            "&de&x&a&d&f&3&f&ds");
    private int islandLevel;
    public IslandUpgradeGUI(int islandLevel) {
            this.islandLevel = islandLevel;
    }

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

        List<IslandLevel> levels = HSProgression.getApi().getIslandLevels();

        for(int i = 0; i < levels.size(); i++) {
            IslandLevel level = levels.get(i);

            inv.setItem(i, level.toDisplayItem(islandLevel));
        }
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 18, TITLE);

        addContent(inv);

        return inv;
    }
}
