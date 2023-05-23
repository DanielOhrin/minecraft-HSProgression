package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.Arrays;

/**
 * Handles TrackedCrops on islands
 */
public class BlockGrowHandler implements Listener {
    private final HSProgressionAPI API;

    public BlockGrowHandler(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockGrow(BlockGrowEvent e) {
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getBlock().getLocation());
        if (island != null) {
            // Handle edge cases
            // Note: Although this hard-coding is not ideal, it is very easy to edit/remove still
            Material material = e.getNewState().getType();
            switch (material) {
                case SUGAR_CANE:
                    this.API.incrementIslandData(island.getUniqueId(), IslandDataType.FARMING, "sugar-cane");
                    return;
                case CACTUS:
                    this.API.incrementIslandData(island.getUniqueId(), IslandDataType.FARMING, "cactus");
                    return;
                case MELON:
                    this.API.incrementIslandData(island.getUniqueId(), IslandDataType.FARMING, "melon");
                    return;
                case PUMPKIN:
                    this.API.incrementIslandData(island.getUniqueId(), IslandDataType.FARMING, "pumpkin");
                    return;
                case PUMPKIN_STEM:
                case MELON_STEM:
                    return;
                default:
                    break;
            }
            if (e.getNewState().getBlockData() instanceof Ageable) {
                Ageable ageData = (Ageable) e.getNewState().getBlockData();
                // If crop is fully grown

                if (ageData.getAge() == ageData.getMaximumAge()) {
                    // Now check if the block is being tracked, and increment it if so.
                    Arrays.stream(TrackedCrop.values())
                            .filter(ct -> ct.getMaterial() == material)
                            .findFirst()
                            .ifPresent(trackedCrop -> {
                                this.API.incrementIslandData(island.getUniqueId(),
                                        IslandDataType.FARMING, trackedCrop.getValue());
                            });
                }
            }
        }
    }
}
