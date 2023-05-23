package net.highskiesmc.progression.events.events;

import com.bgsoftware.superiorskyblock.api.events.IslandEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
import net.highskiesmc.progression.enums.TrackedEntity;
import net.highskiesmc.progression.enums.TrackedNode;

/**
 * Called when an island upgrade is purchased from HSProgression
 */
public class IslandUpgradedEvent extends IslandEvent {
    private final IslandDataType ISLAND_DATA_TYPE;
    private final String KEY;

    public IslandUpgradedEvent(Island island, IslandDataType islandDataType, String key) {
        super(island);
        this.ISLAND_DATA_TYPE = islandDataType;
        this.KEY = key;
    }

    public IslandDataType getIslandDataType() {
        return this.ISLAND_DATA_TYPE;
    }

    /**
     * @return Key for the unlocked item in config.yml
     */
    public String getUnlockedKey() {
        return this.KEY;
    }
}
