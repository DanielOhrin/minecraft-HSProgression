package net.highskiesmc.progression.events.events;

import com.bgsoftware.superiorskyblock.api.events.IslandEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.enums.IslandDataType;

/**
 * Called when an island upgrade becomes ready to be purchased
 */
public class IslandProgressedEvent extends IslandEvent {
    private final IslandDataType ISLAND_DATA_TYPE;
    public IslandProgressedEvent(Island island, IslandDataType islandDataType) {
        super(island);
        this.ISLAND_DATA_TYPE = islandDataType;
    }
    public IslandDataType getIslandDataType() {
        return this.ISLAND_DATA_TYPE;
    }
}
