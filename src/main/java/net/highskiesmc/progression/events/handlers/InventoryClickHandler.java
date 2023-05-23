package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
import net.highskiesmc.progression.enums.TrackedEntity;
import net.highskiesmc.progression.enums.TrackedNode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InventoryClickHandler implements Listener {
    private final HSProgressionAPI API;

    public InventoryClickHandler(HSProgressionAPI api) {
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
                        //TODO: Create new GUI to confirm purchase
                        System.out.println("Yup this works");
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
                        //TODO: Create new GUI to confirm purchase
                        System.out.println("Yup this works");
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
                        //TODO: Create new GUI to confirm purchase
                        System.out.println("Yup this works");
                    }
                }
                } else if (title.equals(IslandDataType.FISHING.getGUITitle())) {
                    e.setCancelled(true);

                }
            }
        }
    }
