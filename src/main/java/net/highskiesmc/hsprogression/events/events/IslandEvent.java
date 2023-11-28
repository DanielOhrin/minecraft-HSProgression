package net.highskiesmc.hsprogression.events.events;

import net.highskiesmc.hsprogression.api.Island;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IslandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    protected final Island island;

    public IslandEvent(Island island) {
        super(!Bukkit.isPrimaryThread());
        this.island = island;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Island getIsland() {
        return this.island;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
