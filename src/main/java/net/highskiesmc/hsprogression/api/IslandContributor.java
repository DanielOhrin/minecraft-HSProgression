package net.highskiesmc.hsprogression.api;

import org.bukkit.entity.EntityType;

import java.util.*;

public class IslandContributor {
    private final UUID playerUuid;
    private final Map<UUID, SlayerContribution> slayerContributions;
    public IslandContributor(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.slayerContributions = new HashMap<>();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Map<UUID, SlayerContribution> getSlayerContributions() {
        return slayerContributions;
    }

    public void addSlayerContribution(UUID islandUuid, EntityType entity, int amount) {
        if (slayerContributions.containsKey(islandUuid)) {
            slayerContributions.get(islandUuid).contribute(entity, amount);
        } else {
            slayerContributions.put(islandUuid, new SlayerContribution(islandUuid, entity, amount));
        }
    }
}
