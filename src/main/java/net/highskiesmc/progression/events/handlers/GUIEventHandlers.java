package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgression;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.*;
import net.highskiesmc.progression.events.events.IslandUpgradedEvent;
import net.highskiesmc.progression.util.UpgradeGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIEventHandlers implements Listener {
    private final HSProgressionAPI API;
    private final Map<UUID, UpgradeGUI> OPEN_CONFIRMATION_INVENTORIES = new HashMap<>();

    public GUIEventHandlers(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        Island island = SuperiorSkyblockAPI.getPlayer(e.getWhoClicked().getUniqueId()).getIsland();

        if (island != null) {
            if (title.equals(IslandDataType.SLAYER.getGUITitle())) {
                e.setCancelled(true);
                int[] slots = new int[]{9, 18, 27, 28, 29, 20, 11, 12, 13, 22, 31, 32, 33, 24, 15, 16, 17};
                Map<Integer, TrackedEntity> trackedSlotMap = new HashMap<>();
                TrackedEntity[] trackedEntities = TrackedEntity.values();

                // i = 1 to skip the one that is unlocked by default.
                for (int i = 1; i < slots.length; i++) {
                    trackedSlotMap.put(slots[i], trackedEntities[i]);
                }

                TrackedEntity entityType = trackedSlotMap.getOrDefault(e.getRawSlot(), null);
                if (entityType != null) {
                    final ConfigurationSection ISLAND_DATA = this.API.getIslandData(island.getUniqueId(),
                            IslandDataType.SLAYER, entityType.getValue());

                    // If it is NOT unlocked, AND the conditions are met for an upgrade
                    if (!ISLAND_DATA.getBoolean("unlocked") && ISLAND_DATA.getBoolean("conditions-met")) {
                        UpgradeGUI gui = this.createConfirmationInventory((Player) e.getWhoClicked(),
                                IslandDataType.SLAYER,
                                this.API.getConfig(IslandDataType.SLAYER).getDouble(entityType.getValue() + ".price"),
                                null,
                                null,
                                entityType,
                                null);
                        this.OPEN_CONFIRMATION_INVENTORIES.put(e.getWhoClicked().getUniqueId(), gui);
                        e.getView().close();
                        e.getWhoClicked().openInventory(gui.getInventory());
                    }
                }
            } else if (title.equals(IslandDataType.MINING.getGUITitle())) {
                e.setCancelled(true);

                int[] slots = new int[]{10, 19, 20, 21, 12, 13, 14, 23, 24, 25, 16};
                Map<Integer, TrackedNode> trackedSlotMap = new HashMap<>();
                TrackedNode[] trackedNodes = TrackedNode.values();

                // i = 1 to skip the one that is unlocked by default.
                for (int i = 1; i < slots.length; i++) {
                    trackedSlotMap.put(slots[i], trackedNodes[i]);
                }

                TrackedNode nodeType = trackedSlotMap.getOrDefault(e.getRawSlot(), null);
                if (nodeType != null) {
                    final ConfigurationSection ISLAND_DATA = this.API.getIslandData(island.getUniqueId(),
                            IslandDataType.MINING, nodeType.getValue());

                    // If it is NOT unlocked, AND the conditions are met for an upgrade
                    if (!ISLAND_DATA.getBoolean("unlocked") && ISLAND_DATA.getBoolean("conditions-met")) {
                        UpgradeGUI gui = this.createConfirmationInventory((Player) e.getWhoClicked(),
                                IslandDataType.MINING,
                                this.API.getConfig(IslandDataType.MINING).getDouble(nodeType.getValue() + ".price"),
                                nodeType,
                                null,
                                null,
                                null);
                        this.OPEN_CONFIRMATION_INVENTORIES.put(e.getWhoClicked().getUniqueId(), gui);
                        e.getView().close();
                        e.getWhoClicked().openInventory(gui.getInventory());
                    }
                }
            } else if (title.equals(IslandDataType.FARMING.getGUITitle())) {
                e.setCancelled(true);

                int[] slots = new int[]{10, 19, 28, 29, 30, 21, 12, 13, 14, 23, 32, 33, 34, 25};
                Map<Integer, TrackedCrop> trackedSlotMap = new HashMap<>();
                TrackedCrop[] trackedCrops = TrackedCrop.values();

                // i = 1 to skip the one that is unlocked by default.
                for (int i = 1; i < slots.length; i++) {
                    trackedSlotMap.put(slots[i], trackedCrops[i]);
                }

                TrackedCrop cropType = trackedSlotMap.getOrDefault(e.getRawSlot(), null);
                if (cropType != null) {
                    final ConfigurationSection ISLAND_DATA = this.API.getIslandData(island.getUniqueId(),
                            IslandDataType.FARMING, cropType.getValue());

                    // If it is NOT unlocked, AND the conditions are met for an upgrade
                    if (!ISLAND_DATA.getBoolean("unlocked") && ISLAND_DATA.getBoolean("conditions-met")) {
                        UpgradeGUI gui = this.createConfirmationInventory((Player) e.getWhoClicked(),
                                IslandDataType.FARMING,
                                this.API.getConfig(IslandDataType.FARMING).getDouble(cropType.getValue() + ".price"),
                                null,
                                cropType,
                                null,
                                null);
                        this.OPEN_CONFIRMATION_INVENTORIES.put(e.getWhoClicked().getUniqueId(), gui);
                        e.getView().close();
                        e.getWhoClicked().openInventory(gui.getInventory());
                    }
                }
            } else if (title.equals(IslandDataType.FISHING.getGUITitle())) {
                e.setCancelled(true);

                int[] slots = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
                Map<Integer, TrackedFish> trackedSlotMap = new HashMap<>();
                TrackedFish[] trackedFish = TrackedFish.values();

                // i = 1 to skip the one that is unlocked by default.
                for (int i = 1; i < slots.length; i++) {
                    trackedSlotMap.put(slots[i], trackedFish[i]);
                }

                TrackedFish fishType = trackedSlotMap.getOrDefault(e.getRawSlot(), null);
                if (fishType != null) {
                    final ConfigurationSection ISLAND_DATA = this.API.getIslandData(island.getUniqueId(),
                            IslandDataType.FISHING, fishType.getValue());

                    // If it is NOT unlocked, AND the conditions are met for an upgrade
                    if (!ISLAND_DATA.getBoolean("unlocked") && ISLAND_DATA.getBoolean("conditions-met")) {
                        UpgradeGUI gui = this.createConfirmationInventory((Player) e.getWhoClicked(),
                                IslandDataType.FISHING,
                                this.API.getConfig(IslandDataType.FISHING).getDouble(fishType.getValue() + ".price"),
                                null,
                                null,
                                null,
                                fishType);
                        this.OPEN_CONFIRMATION_INVENTORIES.put(e.getWhoClicked().getUniqueId(), gui);
                        e.getView().close();
                        e.getWhoClicked().openInventory(gui.getInventory());
                    }
                }
            } else {
                this.OPEN_CONFIRMATION_INVENTORIES
                        .entrySet()
                        .stream()
                        .filter(x -> x.getValue().getInventory().equals(e.getInventory()))
                        .findFirst()
                        .ifPresent(entry -> {
                            e.setCancelled(true);
                            UpgradeGUI upgradeGUI = entry.getValue();

                            switch (e.getRawSlot()) {
                                case 12: // Cancel
                                    e.getView().close();
                                    this.OPEN_CONFIRMATION_INVENTORIES.remove(entry.getKey());
                                    break;
                                case 14: // Confirm
                                    if (!this.API.getIslandData(island.getUniqueId(), upgradeGUI.getDataType(),
                                            upgradeGUI.getDataKey()).getBoolean("unlocked")) {
                                        if (HSProgression.getEconomy().has((Player) e.getWhoClicked(),
                                                upgradeGUI.getAmount())) {
                                            // They have the money
                                            HSProgression.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(e.getWhoClicked().getUniqueId()), upgradeGUI.getAmount());
                                            this.API.unlockIslandData(
                                                    island.getUniqueId(),
                                                    upgradeGUI.getDataType(),
                                                    upgradeGUI.getDataKey()
                                            );
                                            Bukkit.getPluginManager().callEvent(new IslandUpgradedEvent(island,
                                                    upgradeGUI.getDataType(), upgradeGUI.getDataKey()));
                                        } else {
                                            // They do not have the money
                                            e.setCancelled(true);
                                            e.getWhoClicked().sendMessage(ChatColor.RED + "Insufficient funds.");
                                        }
                                    } else {
                                        e.getWhoClicked().sendMessage(ChatColor.RED + "This upgrade has already been " +
                                                "purchased.");
                                    }
                                    e.getView().close();
                                    this.OPEN_CONFIRMATION_INVENTORIES.remove(entry.getKey());
                                    break;
                                default:
                                    break;
                            }
                        });
            }
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        this.OPEN_CONFIRMATION_INVENTORIES
                .entrySet()
                .stream()
                .filter(x -> x.getValue().getInventory().equals(e.getInventory()))
                .findFirst()
                .ifPresent(guiEntry -> {
                    if (guiEntry.getValue().getInventory().equals(e.getInventory())) {
                        OPEN_CONFIRMATION_INVENTORIES.remove(e.getPlayer().getUniqueId());
                    }
                });
    }

    private UpgradeGUI createConfirmationInventory(Player player, IslandDataType dataType, double amount,
                                                   TrackedNode node, TrackedCrop crop, TrackedEntity entity,
                                                   TrackedFish fish) {
        String title = ChatColor.GREEN.toString() + ChatColor.BOLD + "Upgrade for $" + amount;

        Inventory inv = Bukkit.createInventory(player, 27, title);
        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "CANCEL");
        cancel.setItemMeta(cancelMeta);

        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "CONFIRM");
        confirm.setItemMeta(confirmMeta);

        inv.setItem(12, cancel);
        inv.setItem(14, confirm);

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }

        return new UpgradeGUI(inv, amount, dataType, node, crop, entity, fish);
    }
}

