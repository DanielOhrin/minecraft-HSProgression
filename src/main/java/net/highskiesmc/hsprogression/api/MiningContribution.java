package net.highskiesmc.hsprogression.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningContribution {
    private final UUID islandUuid;
    private final Map<String, Integer> contributions;

    public MiningContribution(UUID islandUuid, String nodeId, int amount) {
        this.islandUuid = islandUuid;
        this.contributions = new HashMap<>() {{
            put(nodeId, amount);
        }};
    }

    public UUID getIslandUuid() {
        return islandUuid;
    }

    public Map<String, Integer> getContributions() {
        return contributions;
    }

    public void contribute(String nodeId, int amount) {
        if (contributions.containsKey(nodeId)) {
            contributions.put(nodeId, contributions.get(nodeId) + amount);
        } else {
            contributions.put(nodeId, amount);
        }
    }
}
