package net.highskiesmc.progression;

import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
import net.highskiesmc.progression.enums.TrackedEntity;
import net.highskiesmc.progression.enums.TrackedNode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class HSProgressionAPI {
    private final HSProgression MAIN;

    public HSProgressionAPI(HSProgression main) {
        this.MAIN = main;
    }

    public void createIslandData(Island island) {
        final ConfigurationSection ISLANDS = this.MAIN.getIslands();

        final String ISLAND_ID = island.getUniqueId().toString();

        // SLAYER
        for (TrackedEntity entity : TrackedEntity.values()) {
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.SLAYER.getValue() + '.' + entity.getValue() + '.' + "amount",
                    0);
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.SLAYER.getValue() + '.' + entity.getValue() + '.' + "unlocked",
                    false);
        }

        // MINING
        for (TrackedNode node : TrackedNode.values()) {
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.MINING.getValue() + '.' + node.getValue() + '.' + "amount", 0);
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.MINING.getValue() + '.' + node.getValue() + '.' + "unlocked",
                    0);
        }

        // FARMING
        for (TrackedCrop crop : TrackedCrop.values()) {
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.FARMING.getValue() + '.' + crop.getValue() + '.' + "amount",
                    0);
            ISLANDS.set(ISLAND_ID + '.' + IslandDataType.FARMING.getValue() + '.' + crop.getValue() + '.' + "unlocked",
                    0);
        }

        this.MAIN.saveIslands();
    }

    public void increaseIslandData(UUID islandUUID, IslandDataType dataType, String key) {
        final ConfigurationSection ISLAND = this.MAIN.getIslands().getConfigurationSection(islandUUID.toString());

        long currentValue = 0;
        try {
            currentValue = ISLAND.getLong(dataType.getValue() + '.' + key);
        } catch (NullPointerException ignored) {

        }

        ISLAND.set(dataType.getValue() + '.' + key, currentValue + 1);
    }

    public ConfigurationSection getIslandData(UUID islandUUID, IslandDataType dataType, String key) throws NullPointerException {
        final ConfigurationSection ISLAND_DATA =
                this.MAIN.getIslands().getConfigurationSection(islandUUID.toString() + '.' + dataType + '.' + key);

        if (ISLAND_DATA == null) {
            throw new NullPointerException("Configuration section not found: " + islandUUID + '.' + dataType + '.' + key);
        }

        return ISLAND_DATA;
    }
}
