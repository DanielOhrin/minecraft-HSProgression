package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;

/**
 * Updates islands' tracked slayer data
 */
public class EntityDeathHandler implements Listener {
    private final HSProgressionAPI API;

    public EntityDeathHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            Entity killer = entity.getKiller();
            if (killer != null) {
                Island island = SuperiorSkyblockAPI.getIslandAt(entity.getLocation());
                if (island != null) {
                    EntityType entityType = entity.getType();
                    Arrays.stream(TrackedEntity.values())
                            .filter(ent -> ent.getEntityType() == entityType)
                            .findFirst()
                            .ifPresent(trackedEntity -> {
                                this.API.incrementIslandData(island.getUniqueId(), IslandDataType.SLAYER, trackedEntity.getValue());
                            });
                }
            }
        }
    }
}
