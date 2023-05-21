package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgressionAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityHandler implements Listener {
    private final HSProgressionAPI API;
    public EntityDamageByEntityHandler(HSProgressionAPI api) {
        this.API = api;
    }
    @EventHandler
    public void onEntityDeath(EntityDamageByEntityEvent e) {
        if (e.getEntity().isDead()) {
            if (e.getDamager() instanceof Player && !(e.getEntity() instanceof Player)) {
                Island island = SuperiorSkyblockAPI.getIslandAt(e.getEntity().getLocation());

                if (island != null) {

                }
            }
        }
    }
}
