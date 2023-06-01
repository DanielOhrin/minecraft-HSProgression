package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.fishing.events.events.FishCaughtEvent;
import net.highskiesmc.fishing.util.DropEntry;
import net.highskiesmc.progression.HSProgression;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.IslandFishingBuff;
import net.highskiesmc.progression.enums.TrackedFish;
import net.highskiesmc.progression.util.HologramUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;

public class PlayerFishHandlers implements Listener {
    private final HSProgression MAIN;
    private final HSProgressionAPI API;

    public PlayerFishHandlers(HSProgression main, HSProgressionAPI api) {
        this.MAIN = main;
        this.API = api;
    }

    // This handler applies buffs to the caught item(s)
    @EventHandler
    public void onPlayerCatchItems(FishCaughtEvent e) {
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getHook().getLocation());

        if (island != null) {
            final ConfigurationSection FISHING_DATA =
                    this.API.getIslands().getConfigurationSection(island.getUniqueId().toString() + '.' + IslandDataType.FISHING.getValue());

            // Find which milestone the island has unlocked
            String key = null;
            List<String> keys = new ArrayList<>(FISHING_DATA.getKeys(false));
            for (int i = keys.size() - 1; i >= 0; i--) {
                if (FISHING_DATA.getBoolean(keys.get(i) + ".unlocked")) {
                    key = keys.get(i);
                    break;
                }
            }
            if (key == null) {
                key = TrackedFish.values()[0].getValue();
            }

            final String KEY = key;
            TrackedFish milestone =
                    Arrays.stream(TrackedFish.values()).filter(fish -> fish.getValue().equals(KEY)).findFirst().get();


            Map<IslandFishingBuff, Double> buffs = milestone.getBuffs();

            if (buffs != null) {
                List<DropEntry> drops = e.getDroppedItems();

                if (buffs.containsKey(IslandFishingBuff.DOUBLE_XP)) {
                    double rng = new Random().nextDouble();
                    if (rng <= buffs.get(IslandFishingBuff.DOUBLE_XP)) {
                        for (DropEntry drop : drops) {
                            drop.setExperience(drop.getExperience() * 2);
                        }

                        HologramUtils.spawnAnimated(this.MAIN, e.getHook().getLocation().subtract(0, 1.5, 0),
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "DOUBLE EXP",
                                0, 1);
                    }
                }
                if (buffs.containsKey(IslandFishingBuff.DOUBLE_DROPS)) {
                    double rng = new Random().nextDouble();
                    if (rng <= buffs.get(IslandFishingBuff.DOUBLE_XP)) {
                        for (DropEntry drop : drops) {
                            drop.setAmount(drop.getAmount() * 2);
                        }

                        HologramUtils.spawnAnimated(this.MAIN, e.getHook().getLocation().subtract(0, 1.5, 0),
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "DOUBLE DROPS",
                                0, 1);
                    }

                }

                e.setDroppedItems(drops);
            }
        }
    }


    // This handler just increments the island's /is fishing
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCaughtItems(FishCaughtEvent e) {
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getHook().getLocation());

        if (island != null) {
            final ConfigurationSection FISHING_DATA =
                    this.API.getIslands().getConfigurationSection(island.getUniqueId().toString() + '.' + IslandDataType.FISHING.getValue());

            // Find which milestone the island has unlocked
            String key = null;
            List<String> keys = new ArrayList<>(FISHING_DATA.getKeys(false));
            for (int i = keys.size() - 1; i >= 0; i--) {
                if (FISHING_DATA.getBoolean(keys.get(i) + ".unlocked")) {
                    key = keys.get(i);
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
