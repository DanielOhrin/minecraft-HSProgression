package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class BlockSpreadHandler implements Listener {
    private final HSProgressionAPI API;

    public BlockSpreadHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockSpread(BlockSpreadEvent e) {
        // Handle kelp/bamboo (edge cases)
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getBlock().getLocation());

        if (island != null) {
            switch (e.getSource().getType()) {
                case BAMBOO:
                case BAMBOO_SAPLING:
                    this.API.incrementIslandData(island.getUniqueId(), IslandDataType.FARMING, "bamboo");
                    break;
                case KELP:
                    this.API.incrementIslandData(island.getUniqueId(), IslandDataType.FARMING, "kelp");
                default:
                    break;
            }
        }
    }
}
