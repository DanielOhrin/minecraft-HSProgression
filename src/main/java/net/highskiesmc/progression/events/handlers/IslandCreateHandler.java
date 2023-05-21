package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import net.highskiesmc.progression.HSProgression;
import net.highskiesmc.progression.HSProgressionAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// This creates a place for the new island's data upon creation
public class IslandCreateHandler implements Listener {
    private final HSProgressionAPI API;
    public IslandCreateHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler
    public void onIslandCreate(IslandCreateEvent e) {
        this.API.createIslandData(e.getIsland());
    }
}
