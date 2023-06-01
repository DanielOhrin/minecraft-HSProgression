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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
            double rng = new Random().nextDouble();

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

            final String KEY = key;
            TrackedFish milestone =
                    Arrays.stream(TrackedFish.values()).filter(fish -> fish.getValue().equals(KEY)).findFirst().get();


            Map<IslandFishingBuff, Double> buffs = milestone.getBuffs();

            if (buffs != null) {
                List<DropEntry> drops = e.getDroppedItems();

                if (buffs.containsKey(IslandFishingBuff.DOUBLE_XP)) {
                    if (rng <= buffs.get(IslandFishingBuff.DOUBLE_XP)) {
                        for (DropEntry drop : drops) {
                            drop.setExperience(drop.getExperience() * 2);
                        }

                        HologramUtils.spawnAnimated(this.MAIN, e.getHook().getLocation(),
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "DOUBLE EXP",
                                1, 1);
                    }
                }
                if (buffs.containsKey(IslandFishingBuff.DOUBLE_DROPS)) {
                    if (rng <= buffs.get(IslandFishingBuff.DOUBLE_XP)) {
                        for (DropEntry drop : drops) {
                            drop.setAmount(drop.getAmount() * 2);
                        }

                        HologramUtils.spawnAnimated(this.MAIN, e.getHook().getLocation(),
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "DOUBLE DROPS",
                                1, 1);
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
