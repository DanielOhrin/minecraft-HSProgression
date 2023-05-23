package net.highskiesmc.progression.events.handlers;

import net.highskiesmc.nodes.events.events.IslandNodeMineEvent;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class IslandNodeMineHandler implements Listener {
    private final HSProgressionAPI API;

    public IslandNodeMineHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNodeMineOnIsland(IslandNodeMineEvent e) {
        // All the conditions are already checked, so we just have to increment the value
        this.API.incrementIslandData(e.getIsland().getUniqueId(), IslandDataType.MINING, e.getNode().getType());
    }
}
