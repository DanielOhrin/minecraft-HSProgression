package net.highskiesmc.progression.events.handlers;

import net.highskiesmc.progression.events.events.IslandProgressedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IslandProgressedHandler implements Listener {
    @EventHandler
    public void onIslandProgress(IslandProgressedEvent e) {
        /* TODO:
              Loop through every player
              Alert them that they have progressed
        */
        System.out.println("Island progressed!");
    }
}
