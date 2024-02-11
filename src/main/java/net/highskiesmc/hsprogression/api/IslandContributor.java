package net.highskiesmc.hsprogression.api;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class IslandContributor {
    private final UUID playerUuid;
    private final Map<UUID, SlayerContribution> slayerContributions;
    private final Map<UUID, FarmingContribution> farmingContributions;

    public IslandContributor(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.slayerContributions = new HashMap<>();
        this.farmingContributions = new HashMap<>();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Map<UUID, SlayerContribution> getSlayerContributions() {
        return slayerContributions;
    }

    public Map<UUID, FarmingContribution> getFarmingContributions() {
        return farmingContributions;
    }

    public void addSlayerContribution(UUID islandUuid, EntityType entity, int amount) {
        if (slayerContributions.containsKey(islandUuid)) {
            slayerContributions.get(islandUuid).contribute(entity, amount);
        } else {
            slayerContributions.put(islandUuid, new SlayerContribution(islandUuid, entity, amount));
        }
    }

    public void addFarmingContribution(UUID islandUuid, Material crop, int amount) {
        if (farmingContributions.containsKey(islandUuid)) {
            farmingContributions.get(islandUuid).contribute(crop, amount);
        } else {
            farmingContributions.put(islandUuid, new FarmingContribution(islandUuid, crop, amount));
        }
    }
}
