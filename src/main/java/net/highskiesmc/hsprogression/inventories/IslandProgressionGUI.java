package net.highskiesmc.hsprogression.inventories;

import net.highskiesmc.hscore.configuration.Config;
import net.highskiesmc.hscore.inventory.GUI;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.DisplayableItem;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.api.Island;
import net.highskiesmc.hsprogression.api.IslandProgressionType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IslandProgressionGUI implements GUI {
    private final HSProgression main;
    private final HSProgressionApi api;
    private final Player player;
    private final Island island;
    private final Config config;
    private List<Integer> slots;
    private int guiSize;
    private final IslandProgressionType type;

    public IslandProgressionGUI(HSProgression main, Player player, Island island, IslandProgressionType type) {
        this.main = main;
        this.api = HSProgression.getApi();
        this.config = main.getConfigs();
        this.player = player;
        this.island = island;
        this.type = type;
    }

    public IslandProgressionGUI(HSProgression main, Player player,
                                com.bgsoftware.superiorskyblock.api.island.Island sIsland,
                                IslandProgressionType type) {
        this.main = main;
        this.api = HSProgression.getApi();
        this.config = main.getConfigs();
        this.player = player;
        this.island = api.getIsland(sIsland);
        this.type = type;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public void onInventoryOpen(InventoryOpenEvent e) {

    }

    @Override
    public void onInventoryClose(InventoryCloseEvent e) {
// TODO: FARMING RECIPES

    }

    @Override
    public void addContent(Inventory inv) {
        List<DisplayableItem> levelItems = api.getLevelItems(type);

        if (levelItems == null || levelItems.isEmpty()) {
            main.getLogger().warning(String.format("Found null/empty list of DisplayableItems. Type: %s", type));
            return;
        }

        List<ItemStack> items = levelItems.stream().map(s -> s.toDisplayItem(island, config)).toList();

        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            int slot = slots.get(i);

            inv.setItem(slot, item);
        }
        ItemStack filler = new ItemStack(Material.valueOf(config.get("gui.filler.material", String.class,
                "BLACK_STAINED_GLASS_PANE")));
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName("");
        filler.setItemMeta(meta);

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                inv.setItem(i, filler);
            }
        }
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        slots =
                Arrays.stream(config.get(type.name().toLowerCase() + ".gui.slots", String.class, "6-9").split("-")).map(Integer::valueOf).collect(Collectors.toList());
        guiSize = Math.min(54, 9 + Math.max(9, ((int) Math.ceil((double) Collections.max(slots) / 9)) * 9));

        Inventory inv = Bukkit.createInventory(this, guiSize,
                TextUtils.translateColor(config.get("island-" + type.name().toLowerCase() + "-menu-title",
                                String.class,
                                "&x&0&5&a&f&c&6&lI&x&0&c&b&6&c&b&ls&x&1&4&b&d&d&0&ll&x&1&b&c&4&d&5&la&x&2&3&c&b&d&a" +
                                        "&ln&x&2&a&d&2&d&f&ld &x&3&2&d&a&e&4&lS&x&3&9&e&1&e&9&ll&x&4&1&e&8&e&e&la&x&4" +
                                        "&8&e&f&f&3&ly&x&5&0&f&6&f&8&le&x&5&7&f&d&f&d&lr"
                        )
                )
        );

        addContent(inv);

        return inv;
    }
}