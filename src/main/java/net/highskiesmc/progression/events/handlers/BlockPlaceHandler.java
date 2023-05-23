package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;
import java.util.Optional;

public class BlockPlaceHandler implements Listener {
    private final HSProgressionAPI API;

    public BlockPlaceHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        // Check if was on island and player has permission
        Player player = e.getPlayer();
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getBlockPlaced().getLocation());
        if (island != null
                && island.hasPermission(SuperiorSkyblockAPI.getPlayer(player), IslandPrivilege.getByName("BUILD"))) {
            Material material = e.getBlockPlaced().getType();

            // Make sure the crop is not the default one, and that it is tracked.
            if (material != TrackedCrop.values()[0].getMaterial()) {
                Optional<TrackedCrop> optionalTrackedCrop = Arrays.stream(TrackedCrop.values())
                        .filter(ct -> ct.getMaterial() == material)
                        .findFirst();
                if (optionalTrackedCrop.isPresent()) {
                    TrackedCrop trackedCrop = optionalTrackedCrop.get();
                    final ConfigurationSection ISLAND_DATA = this.API.getIslandData(island.getUniqueId(),
                            IslandDataType.FARMING, trackedCrop.getValue());

                    if (ISLAND_DATA != null) {
                        if (!ISLAND_DATA.getBoolean("unlocked")) {
                            e.setCancelled(true);
                            this.API.sendNotUnlocked(player);
                        }
                    }

                }
            }
        }
    }
}
