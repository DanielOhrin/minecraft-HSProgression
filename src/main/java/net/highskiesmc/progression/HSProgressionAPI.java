package net.highskiesmc.progression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.enums.*;
import net.highskiesmc.progression.events.events.IslandProgressedEvent;
import net.highskiesmc.progression.events.events.IslandUpgradedEvent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HSProgressionAPI {
    private final HSProgression MAIN;
    private final NamespacedKey NAMESPACED_KEY_RECIPE_CROP_TYPE;

    public HSProgressionAPI(HSProgression main) {
        this.MAIN = main;
        this.NAMESPACED_KEY_RECIPE_CROP_TYPE = new NamespacedKey(main, "recipe-crop-type");
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

        // FISHING
        TrackedFish[] trackedFish = TrackedFish.values();
        for (int i = 0; i < trackedFish.length; i++) {
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.FISHING.getValue() + '.' + trackedFish[i].getValue() + '.' +
                    "amount", 0);
            if (i != 0) {
                ISLANDS.set(ISLAND_ID + '.' + IslandDataType.FISHING.getValue() + '.' + trackedFish[i].getValue() + '.' +
                        "conditions-met", false);
                ISLANDS.set(ISLAND_ID + '.' + IslandDataType.FISHING.getValue() + '.' + trackedFish[i].getValue() + '.' +
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
                if (currentValue + 1 >= this.getConfig(dataType).getLong(nextItemKey + '.' + "amount")) {
                    ISLAND_DATA.set(nextItemKey + '.' + "conditions-met", true);
                    if (dataType == IslandDataType.FARMING) {
                        ISLAND_DATA.set(nextItemKey + '.' + "unlocked", true);
                    }
                    // Call Event
                    Bukkit.getPluginManager().callEvent(dataType == IslandDataType.FARMING
                            ? new IslandUpgradedEvent(SuperiorSkyblockAPI.getIslandByUUID(islandUUID), dataType,
                            nextItemKey)
                            : new IslandProgressedEvent(SuperiorSkyblockAPI.getIslandByUUID(islandUUID), dataType,
                            nextItemKey));
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

    /**
     * Bypasses the provided section's conditions for the provided island (Currently used for farming recipe claiming)
     *
     * @param islandUUID UUID of island
     * @param dataType   IslandDataType (mining/slayer/etc.)
     * @param key        Name of tracked item. zombie, coal, etc.
     */
    public void meetIslandDataConditions(UUID islandUUID, IslandDataType dataType, String key) throws NullPointerException {
        final ConfigurationSection ISLAND_DATA =
                this.MAIN.getIslands().getConfigurationSection(islandUUID.toString() + '.' + dataType.getValue() + '.' + key);

        if (ISLAND_DATA == null) {
            throw new NullPointerException("Configuration section not found: " + islandUUID + '.' + dataType.getValue() + '.' + key);
        }

        ISLAND_DATA.set("conditions-met", true);
        this.MAIN.saveIslands();
    }

    public void sendNotUnlocked(Player player) {
        final ConfigurationSection CONFIG = this.getConfig(null).getConfigurationSection("all.locked");

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CONFIG.getString("message")));
        player.playSound(player.getLocation(), Sound.valueOf(CONFIG.getString("sound")), 1, 1);
    }

    public ConfigurationSection getConfig(IslandDataType dataType) {
        if (dataType == null) {
            return this.MAIN.getConfig();
        }

        switch (dataType) {
            case FARMING:
                return this.MAIN.getFarmingConfig();
            case SLAYER:
                return this.MAIN.getSlayerConfig();
            case MINING:
                return this.MAIN.getMiningConfig();
            case FISHING:
                return this.MAIN.getFishingConfig();
            default:
                return null;
        }
    }

    public void saveConfig(IslandDataType dataType) {
        if (dataType == null) {
            this.MAIN.saveConfig();
            return;
        }

        switch (dataType) {
            case FARMING:
                this.MAIN.saveFarmingConfig();
                break;
            case SLAYER:
                this.MAIN.saveSlayerConfig();
                break;
            case MINING:
                this.MAIN.saveMiningConfig();
                break;
            case FISHING:
                this.MAIN.saveFishingConfig();
            default:
                break;
        }
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

    /**
     * @return Full recipe for specified TrackedCrop
     */
    public ItemStack getFullRecipe(TrackedCrop crop) {
        final ConfigurationSection RECIPE_CONFIG =
                this.getConfig(IslandDataType.FARMING).getConfigurationSection("recipe");

        // Convert the name to "title case"
        String current =
                Arrays.stream(crop.getValue().split("-")).map(x -> x.substring(0, 1).toUpperCase() + x.substring(1)).collect(Collectors.joining(" "));

        ItemStack recipe = new ItemStack(Material.PAPER);
        ItemMeta meta = recipe.getItemMeta();

        // Set the display name
        String displayName = RECIPE_CONFIG.getString("display-name")
                .replace("{current}", current);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        // Set the lore
        List<String> lore = RECIPE_CONFIG.getStringList("lore");
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i).replace("{current}", current);
            lore.set(i, ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(lore);

        // Apply the persistent data
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(this.NAMESPACED_KEY_RECIPE_CROP_TYPE, PersistentDataType.STRING, crop.getValue());

        recipe.setItemMeta(meta);
        return recipe;
    }

    public NamespacedKey getRecipeCropTypeKey() {
        return this.NAMESPACED_KEY_RECIPE_CROP_TYPE;
    }

    public void fullyUnlockIslandDataUpTo(UUID islandUUID, IslandDataType dataType, String finalKey) {
        switch (dataType) {
            case FARMING:
                for (String key :
                        Arrays.stream(TrackedCrop.values()).map(TrackedCrop::getValue).collect(Collectors.toList())) {
                    if (key.equalsIgnoreCase(finalKey)) {
                        break;
                    }
                    fullyUnlockIslandDataWithoutSaving(islandUUID, dataType, key);
                }
                break;
            case MINING:
                for (String key :
                        Arrays.stream(TrackedNode.values()).map(TrackedNode::getValue).collect(Collectors.toList())) {
                    if (key.equalsIgnoreCase(finalKey)) {
                        break;
                    }
                    fullyUnlockIslandDataWithoutSaving(islandUUID, dataType, key);
                }
                break;
            case SLAYER:
                for (String key :
                        Arrays.stream(TrackedEntity.values()).map(TrackedEntity::getValue).collect(Collectors.toList())) {
                    if (key.equalsIgnoreCase(finalKey)) {
                        break;
                    }
                    fullyUnlockIslandDataWithoutSaving(islandUUID, dataType, key);
                }
                break;
            case FISHING:
                for (String key:
                        Arrays.stream(TrackedFish.values()).map(TrackedFish::getValue).collect(Collectors.toList())) {
                    if (key.equalsIgnoreCase(finalKey)) {
                        break;
                    }
                    fullyUnlockIslandDataWithoutSaving(islandUUID, dataType, key);
                }
                break;
            default:
                break;
        }
        fullyUnlockIslandDataWithoutSaving(islandUUID, dataType, finalKey);
        saveIslands();
    }

    private void fullyUnlockIslandDataWithoutSaving(UUID islandUUID, IslandDataType dataType, String key) {
        final ConfigurationSection ISLAND_DATA = this.getIslandData(islandUUID, dataType, key);
        ISLAND_DATA.set("conditions-met", true);
        ISLAND_DATA.set("unlocked", true);
    }
}
