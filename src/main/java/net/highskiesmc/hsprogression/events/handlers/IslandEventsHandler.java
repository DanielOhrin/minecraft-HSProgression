package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.PlayerChangeRoleEvent;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class IslandEventsHandler extends HSListener {
    public IslandEventsHandler(HSProgression main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandCreated(IslandCreateEvent e) {
        HSProgression.getApi().createIsland(e.getIsland());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandDisband(IslandDisbandEvent e) {
        HSProgression.getApi().deleteIsland(e.getIsland());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLeaderChange(PlayerChangeRoleEvent e) {
        if (e.getNewRole().isLastRole()) {
            HSProgression.getApi().setIslandLeader(e.getPlayer().getIsland(), e.getPlayer());
        }
    }
}
