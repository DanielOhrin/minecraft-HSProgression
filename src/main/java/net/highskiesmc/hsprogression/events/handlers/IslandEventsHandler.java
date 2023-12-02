package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandTransferEvent;
import com.bgsoftware.superiorskyblock.api.events.PlayerChangeRoleEvent;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class IslandEventsHandler extends HSListener {
    private final HSProgressionApi api;
    public IslandEventsHandler(HSProgression main, HSProgressionApi api) {
        super(main);

        this.api = api;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandCreated(IslandCreateEvent e) {
        api.createIsland(e.getIsland());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandDisband(IslandDisbandEvent e) {
        api.deleteIsland(e.getIsland());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandLeaderChange(IslandTransferEvent e) {
        api.setIslandLeader(e.getNewOwner().getIsland(), e.getNewOwner());
    }
}
