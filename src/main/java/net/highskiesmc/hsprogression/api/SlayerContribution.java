package net.highskiesmc.hsprogression.api;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlayerContribution {
    private final UUID islandUuid;
    private final Map<EntityType, Integer> contributions;
    public SlayerContribution(UUID islandUuid, EntityType entity, int amount) {
        this.islandUuid = islandUuid;
        this.contributions = new HashMap<>()
        {{
            put(entity, amount);
        }};
    }

    public UUID getIslandUuid() {
        return islandUuid;
    }

    public Map<EntityType, Integer> getContributions() {
        return contributions;
    }

    public void contribute(EntityType entity, int amount) {
        if (contributions.containsKey(entity)) {
            contributions.put(entity, contributions.get(entity) + amount);
        } else {
            contributions.put(entity, amount);
        }
    }
}
