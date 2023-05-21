package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.wildstacker.api.events.PlaceEvent;
import com.bgsoftware.wildstacker.api.events.SpawnerPlaceEvent;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;

public class SpawnerPlaceHandler implements Listener {
    private final HSProgressionAPI API;

    public SpawnerPlaceHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler
    public void onSpawnerPlace(SpawnerPlaceEvent e) {
        // Check if was placed on an island
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getSpawner().getLocation());
        if (island != null) {
            EntityType entityType = e.getSpawner().getSpawnedType();

            // Make sure the entity is not the default one, and that it is tracked.
            if (entityType != TrackedEntity.values()[0].getEntityType()) {
                Arrays.stream(TrackedEntity.values())
                        .filter(ent -> ent.getEntityType() == entityType)
                        .findFirst()
                        .ifPresent(trackedEntity -> {
                            final ConfigurationSection ISLAND_DATA = this.API.getIslandData(island.getUniqueId(),
                                    IslandDataType.SLAYER, trackedEntity.getValue());

                            if (ISLAND_DATA != null) {
                                if (!ISLAND_DATA.getBoolean("unlocked")) {
                                    e.setCancelled(true);
                                    Player player = e.getPlayer();
                                    player.sendMessage(ChatColor.RED + "[!] Island has not unlocked that yet!");
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                                }
                            }
                        });
            }
        }
    }
}
