package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.nodes.events.events.IslandNodePlaceEvent;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedNode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class IslandNodePlaceHandler implements Listener {
    private final HSProgressionAPI API;

    public IslandNodePlaceHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler
    public void onNodePlaceOnIsland(IslandNodePlaceEvent e) {
        // Check if was placed on an island
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getNode().getLocationParsed());
        if (island != null) {
            String nodeType = e.getNode().getType();

            // Make sure the node is not the default one, and that it is tracked.
            if (!nodeType.equals(TrackedNode.values()[0].getValue())) {
                Arrays.stream(TrackedNode.values())
                        .filter(nt -> nt.getValue().equals(nodeType))
                        .findFirst()
                        .ifPresent(trackedNode -> {
                            final ConfigurationSection ISLAND_DATA = this.API.getIslandData(island.getUniqueId(),
                                    IslandDataType.MINING, trackedNode.getValue());

                            if (ISLAND_DATA != null) {
                                if (!ISLAND_DATA.getBoolean("unlocked")) {
                                    e.setCancelled(true);
                                    Player player = e.getPlayer();
                                    this.API.sendNotUnlocked(player);
                                }
                            }
                        });
            }
        }
    }
}
