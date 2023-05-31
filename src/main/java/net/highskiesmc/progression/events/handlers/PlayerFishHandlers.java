package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.fishing.events.events.FishCaughtEvent;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedFish;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerFishHandlers implements Listener {
    private final HSProgressionAPI API;

    public PlayerFishHandlers(HSProgressionAPI api) {
        this.API = api;
    }

    // This handler just increments the island's /is fishing
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCatchFish(FishCaughtEvent e) {
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getHook().getLocation());

        if (island != null) {
            final ConfigurationSection FISHING_DATA =
                    this.API.getIslands().getConfigurationSection(island.getUniqueId().toString() + '.' + IslandDataType.FISHING.getValue());

            // Find which milestone the island has unlocked
            String key = null;
            for (String KEY : FISHING_DATA.getKeys(false)) {
                if (FISHING_DATA.getBoolean(KEY + ".unlocked")) {
                    key = KEY;
                    break;
                }
            }
            if (key == null) {
                key = TrackedFish.values()[0].getValue();
            }

            // Increment their amount caught
            int amountCaught = e.getDroppedItems().size();
            for (int i = 0; i < amountCaught; i++) {
                this.API.incrementIslandData(island.getUniqueId(), IslandDataType.FISHING, key);
            }
        }
    }
}
