package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.collect.Sets;
import net.highskiesmc.fishing.events.events.FishCaughtEvent;
import net.highskiesmc.fishing.util.DropEntry;
import net.highskiesmc.fishing.util.enums.Rarity;
import net.highskiesmc.progression.HSProgression;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.IslandFishingBuff;
import net.highskiesmc.progression.enums.TrackedFish;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerFishHandlers implements Listener {
    private static final double ISLAND_FISH_CHANCE = 0.75D;
    private final HSProgression MAIN;
    private final HSProgressionAPI API;

    public PlayerFishHandlers(HSProgression main, HSProgressionAPI api) {
        this.MAIN = main;
        this.API = api;
    }

    // This handler just increments the island's /is fishing
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCaughtItems(FishCaughtEvent e) {
        Island island = SuperiorSkyblockAPI.getIslandAt(e.getPlayer().getLocation());

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


            // Roll the chance to override their drops
            if (new Random().nextDouble() <= ISLAND_FISH_CHANCE) {
                final TrackedFish HIGHEST_UNLOCKED = TrackedFish.fromValue(key);
                final List<TrackedFish> UNLOCKED_FISH =
                        Arrays.stream(TrackedFish.values()).filter(x -> x.ordinal() <= HIGHEST_UNLOCKED.ordinal()).collect(Collectors.toList());

                Collections.shuffle(UNLOCKED_FISH);

                final TrackedFish CAUGHT_FISH = UNLOCKED_FISH.get(0);
                final int CAUGHT_AMOUNT =
                        new Random().nextInt(1,
                                Math.max(2,
                                        (int) Math.ceil((UNLOCKED_FISH.size() - (double) (CAUGHT_FISH.ordinal() + 1)) / 3) + 1));

                ItemStack item = CAUGHT_FISH.getItemStack();
                item.setAmount(CAUGHT_AMOUNT);

                // Increment their amount caught
                for (int i = 0; i < CAUGHT_AMOUNT; i++) {
                    this.API.incrementIslandData(island.getUniqueId(), IslandDataType.FISHING, CAUGHT_FISH.getValue());
                }

                DropEntry dropEntry = new DropEntry(item, 0, CAUGHT_FISH.getXp() * CAUGHT_AMOUNT);
                dropEntry.setRarity(Rarity.ISLAND);
                e.setDroppedItems(Collections.singletonList(dropEntry));
            }
        } else {
            // Roll the chance to override their drops
            if (new Random().nextDouble() <= ISLAND_FISH_CHANCE) {
                e.setDroppedItems(new ArrayList<>());
                e.getPlayer().sendMessage(ChatColor.RED + "The fish got away. You need an island to catch it!");
            }
        }
    }
}
