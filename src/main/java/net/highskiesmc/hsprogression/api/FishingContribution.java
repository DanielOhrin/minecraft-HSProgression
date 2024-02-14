package net.highskiesmc.hsprogression.api;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishingContribution {
    private final UUID islandUuid;
    private final Map<String, Integer> contributions;

    public FishingContribution(UUID islandUuid, String fishId, int amount) {
        this.islandUuid = islandUuid;
        this.contributions = new HashMap<>() {{
            put(fishId, amount);
        }};
    }

    public UUID getIslandUuid() {
        return islandUuid;
    }

    public Map<String, Integer> getContributions() {
        return contributions;
    }

    public void contribute(String fishId, int amount) {
        if (contributions.containsKey(fishId)) {
            contributions.put(fishId, contributions.get(fishId) + amount);
        } else {
            contributions.put(fishId, amount);
        }
    }
}
