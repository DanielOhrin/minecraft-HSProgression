package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandInviteEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandJoinEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class IslandLevelRestrictionsHandler extends HSListener {
    private final HSProgressionApi api;
    public IslandLevelRestrictionsHandler(HSPlugin main, HSProgressionApi api) {
        super(main);

        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlaceOnIsland(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        Island sIsland = SuperiorSkyblockAPI.getIslandAt(block.getLocation());

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null) {
            return;
        }

        if (!api.canPlace(island, e.getItemInHand())) {
            e.setCancelled(true);

            // TODO: make configurable message for this...
            // TODO: add a {label} placeholder for the item's label... and {required-level} and {level}
            e.getPlayer().sendMessage("You cant place that yet...");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInviteToIsland(IslandInviteEvent e) {
        Island sIsland = e.getIsland();

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null) {
            return;
        }

        int members = sIsland.getIslandMembers(true).size();
        int memberLimit = api.getIslandLevel(island.getLevel()).getMaxMembers();

        if (memberLimit == members) {
            // TODO: Make configurable msg
            e.setCancelled(true);
            e.getPlayer().asPlayer().sendMessage("Erm... member limit reached.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onJoinIsland(IslandJoinEvent e) {
        Island sIsland = e.getIsland();

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null) {
            return;
        }

        int members = sIsland.getIslandMembers(true).size();
        int memberLimit = api.getIslandLevel(island.getLevel()).getMaxMembers();

        if (memberLimit == members) {
            // TODO: Make configurable msg
            e.setCancelled(true);
            e.getPlayer().asPlayer().sendMessage("Erm... that island is full.");
        }
    }
}
