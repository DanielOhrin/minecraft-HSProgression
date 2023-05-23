package net.highskiesmc.progression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
import net.highskiesmc.progression.enums.TrackedEntity;
import net.highskiesmc.progression.enums.TrackedNode;
import net.highskiesmc.progression.events.events.IslandProgressedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HSProgressionAPI {
    private final HSProgression MAIN;

    public HSProgressionAPI(HSProgression main) {
        this.MAIN = main;
    }

    /**
     * @param island Island to create data for
     * @return ConfigurationSection that was just created for the island
     */
    public ConfigurationSection createIslandData(Island island) {
        final ConfigurationSection ISLANDS = this.MAIN.getIslands();

        final String ISLAND_ID = island.getUniqueId().toString();

        // SLAYER
        TrackedEntity[] trackedEntities = TrackedEntity.values();
        for (int i = 0; i < trackedEntities.length; i++) {
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.SLAYER.getValue() + '.' + trackedEntities[i].getValue() + '.' + "amount",
                    0);
            if (i != 0) {
                ISLANDS.set(ISLAND_ID + '.' + IslandDataType.SLAYER.getValue() + '.' + trackedEntities[i].getValue() + '.' +
                        "conditions-met", false);
                ISLANDS.set(ISLAND_ID + '.' + IslandDataType.SLAYER.getValue() + '.' + trackedEntities[i].getValue() + '.' +
                        "unlocked", false);
            }
        }

        // MINING
        TrackedNode[] trackedNodes = TrackedNode.values();
        for (int i = 0; i < trackedNodes.length; i++) {
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.MINING.getValue() + '.' + trackedNodes[i].getValue() + '.' +
                    "amount", 0);
            if (i != 0) {
                ISLANDS.set(ISLAND_ID + '.' + IslandDataType.MINING.getValue() + '.' + trackedNodes[i].getValue() + '.' +
                        "conditions-met", false);
                ISLANDS.set(ISLAND_ID + '.' + IslandDataType.MINING.getValue() + '.' + trackedNodes[i].getValue() + '.' +
                        "unlocked", false);
            }
        }

        // FARMING
        TrackedCrop[] trackedCrops = TrackedCrop.values();
        for (int i = 0; i < trackedCrops.length; i++) {
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.FARMING.getValue() + '.' + trackedCrops[i].getValue() + '.' +
                    "amount", 0);
            if (i != 0) {
                ISLANDS.set(ISLAND_ID + '.' + IslandDataType.FARMING.getValue() + '.' + trackedCrops[i].getValue() + '.' +
                        "conditions-met", false);
                ISLANDS.set(ISLAND_ID + '.' + IslandDataType.FARMING.getValue() + '.' + trackedCrops[i].getValue() + '.' +
                        "unlocked", false);
            }
        }

        this.MAIN.saveIslands();
        return ISLANDS.getConfigurationSection(island.getUniqueId().toString());
    }

    /**
     * Increments the provided island's data value by 1. Calls IslandProgressionEvent when condition for next unlock
     * is met
     *
     * @param islandUUID Island's UUID
     * @param dataType   IslandDataType (Slayer, mining, etc.)
     * @param key        Name of whatever you want to increment. Ex: zombie, coal, magma-cube
     */
    public void incrementIslandData(UUID islandUUID, IslandDataType dataType, String key) {
        ConfigurationSection ISLAND_DATA =
                this.MAIN.getIslands().getConfigurationSection(islandUUID.toString() + '.' + dataType.getValue());
        long currentValue = 0;

        if (ISLAND_DATA == null) {
            ISLAND_DATA =
                    createIslandData(SuperiorSkyblockAPI.getIslandByUUID(islandUUID)).getConfigurationSection(dataType.getValue());
        } else {
            currentValue = ISLAND_DATA.getLong(key + ".amount");
        }

        // Check if this increment unlocked the next upgrade
        List<String> trackedItems = new ArrayList<>(ISLAND_DATA.getKeys(false));
        int indexOfCurrent = trackedItems.indexOf(key);

        if (indexOfCurrent != trackedItems.size() - 1) {
            String nextItemKey = trackedItems.get(indexOfCurrent + 1);
            if (!ISLAND_DATA.getBoolean(nextItemKey + '.' + "conditions-met")) {
                if (currentValue + 1 >= this.MAIN.getConfig().getLong(dataType.getValue() + '.' + nextItemKey + '.' +
                        "amount")) {
                    ISLAND_DATA.set(nextItemKey + '.' + "conditions-met", true);

                    // Call IslandProgressedEvent
                    Bukkit.getPluginManager().callEvent(new IslandProgressedEvent(SuperiorSkyblockAPI.getIslandByUUID(islandUUID), dataType, key));
                }
            }
        }

        // Increment the value
        ISLAND_DATA.set(key + ".amount", currentValue + 1);
        this.MAIN.saveIslands();
    }

    /**
     * @param islandUUID UUID of island
     * @param dataType   IslandDataType (mining/slayer/etc.)
     * @param key        Name of tracked item. zombie, coal, etc.
     * @return ConfigurationSection for that tracked item
     */
    public ConfigurationSection getIslandData(UUID islandUUID, IslandDataType dataType, String key) throws NullPointerException {
        final ConfigurationSection ISLAND_DATA =
                this.MAIN.getIslands().getConfigurationSection(islandUUID.toString() + '.' + dataType.getValue() + '.' + key);

        if (ISLAND_DATA == null) {
            throw new NullPointerException("Configuration section not found: " + islandUUID + '.' + dataType.getValue() + '.' + key);
        }

        return ISLAND_DATA;
    }

    /**
     * Unlocks the provided section for the provided island
     *
     * @param islandUUID UUID of island
     * @param dataType   IslandDataType (mining/slayer/etc.)
     * @param key        Name of tracked item. zombie, coal, etc.
     */
    public void unlockIslandData(UUID islandUUID, IslandDataType dataType, String key) throws NullPointerException {
        final ConfigurationSection ISLAND_DATA =
                this.MAIN.getIslands().getConfigurationSection(islandUUID.toString() + '.' + dataType.getValue() + '.' + key);

        if (ISLAND_DATA == null) {
            throw new NullPointerException("Configuration section not found: " + islandUUID + '.' + dataType.getValue() + '.' + key);
        }

        ISLAND_DATA.set("unlocked", true);
        this.MAIN.saveIslands();
    }

    public void sendNotUnlocked(Player player) {
        final ConfigurationSection CONFIG = this.MAIN.getConfig().getConfigurationSection("all.locked");

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CONFIG.getString("message")));
        player.playSound(player.getLocation(), Sound.valueOf(CONFIG.getString("sound")), 1, 1);
    }

    public ConfigurationSection getConfig() {
        return this.MAIN.getConfig();
    }

    public void saveConfig() {
        this.MAIN.saveConfig();
    }

    public void saveIslands() {
        this.MAIN.saveIslands();
    }

    /**
     * @return ConfigurationSection responsible for islands
     */
    public ConfigurationSection getIslands() {
        return this.MAIN.getIslands();
    }
}
