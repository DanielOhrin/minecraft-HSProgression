package net.highskiesmc.hsprogression.api;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FarmingContribution {
    private final UUID islandUuid;
    private final Map<Material, Integer> contributions;

    public FarmingContribution(UUID islandUuid, Material crop, int amount) {
        this.islandUuid = islandUuid;
        this.contributions = new HashMap<>() {{
            put(crop, amount);
        }};
    }

    public UUID getIslandUuid() {
        return islandUuid;
    }

    public Map<Material, Integer> getContributions() {
        return contributions;
    }

    public void contribute(Material crop, int amount) {
        if (contributions.containsKey(crop)) {
            contributions.put(crop, contributions.get(crop) + amount);
        } else {
            contributions.put(crop, amount);
        }
    }
}
