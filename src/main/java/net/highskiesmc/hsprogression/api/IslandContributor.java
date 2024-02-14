package net.highskiesmc.hsprogression.api;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class IslandContributor {
    private final UUID playerUuid;
    private final Map<UUID, SlayerContribution> slayerContributions;
    private final Map<UUID, FarmingContribution> farmingContributions;
    private final Map<UUID, MiningContribution> miningContributions;
    private final Map<UUID, FishingContribution> fishingContributions;

    public IslandContributor(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.slayerContributions = new HashMap<>();
        this.farmingContributions = new HashMap<>();
        this.miningContributions = new HashMap<>();
        this.fishingContributions = new HashMap<>();
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

    public Map<UUID, MiningContribution> getMiningContributions() {
        return miningContributions;
    }

    public Map<UUID, FishingContribution> getFishingContributions() {
        return fishingContributions;
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
    public void addMiningContribution(UUID islandUuid, String nodeId, int amount) {
        if (miningContributions.containsKey(islandUuid)) {
            miningContributions.get(islandUuid).contribute(nodeId, amount);
        } else {
            miningContributions.put(islandUuid, new MiningContribution(islandUuid, nodeId, amount));
        }
    }
    public void addFishingContribution(UUID islandUuid, String fishId, int amount) {
        if (fishingContributions.containsKey(islandUuid)) {
            fishingContributions.get(islandUuid).contribute(fishId, amount);
        } else {
            fishingContributions.put(islandUuid, new FishingContribution(islandUuid, fishId, amount));
        }
    }
}
