package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.event.SpawnerStackEvent;
import dev.rosewood.rosestacker.nms.spawner.SpawnerType;
import dev.rosewood.rosestacker.stack.Stack;
import dev.rosewood.rosestacker.utils.ItemUtils;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedEntity;
import net.highskiesmc.progression.util.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Optional;

public class SpawnerPlaceHandler implements Listener {
    private final HSProgressionAPI API;

    public SpawnerPlaceHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler
    public void onSpawnerPlace(SpawnerStackEvent e) {
        if (e.isNew()) {
            // Check if was placed on an island
            Island island =
                    SuperiorSkyblockAPI.getIslandAt(e.getStack().getLocation());
            if (island != null) {
                EntityType entityType =
                        EntityType.valueOf(NBTEditor.getString(e.getPlayer().getInventory().getItemInMainHand(),
                                "EntityType"));

                // Make sure the entity is not the default one, and that it is tracked.
                if (entityType != TrackedEntity.values()[0].getEntityType()) {
                    Optional<TrackedEntity> optionalEntity = Arrays.stream(TrackedEntity.values())
                            .filter(ent -> ent.getEntityType() == entityType)
                            .findFirst();

                    if (optionalEntity.isPresent()) {
                        TrackedEntity trackedEntity = optionalEntity.get();

                        final ConfigurationSection ISLAND_DATA = this.API.getIslandData(island.getUniqueId(),
                                IslandDataType.SLAYER, trackedEntity.getValue());

                        if (ISLAND_DATA != null) {
                            if (!ISLAND_DATA.getBoolean("unlocked")) {
                                e.setCancelled(true);
                                Player player = e.getPlayer();
                                this.API.sendNotUnlocked(player);
                            }
                        }
                    } else {
                        e.setCancelled(true);
                        Bukkit.getLogger().severe(e.getPlayer().getName() + " tried to place a " + entityType +
                                "spawner on an island!");
                    }
                }
            }
        }
    }
}
