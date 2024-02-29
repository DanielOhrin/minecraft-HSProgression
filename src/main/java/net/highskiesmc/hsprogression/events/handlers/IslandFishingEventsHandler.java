package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.hsfishing.events.events.FishCaughtEvent;
import net.highskiesmc.hsfishing.util.DropEntry;
import net.highskiesmc.hsfishing.util.enums.Rarity;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.api.FishingLevel;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.api.IslandProgressionType;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class IslandFishingEventsHandler extends HSListener {
    private final HSProgressionApi api;
    private final List<FishingLevel> fish;

    public IslandFishingEventsHandler(HSPlugin main, HSProgressionApi api) {
        super(main);
        this.api = api;
        this.fish = api.getFishingLevels();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onFish(FishCaughtEvent e) {
        Island sIsland = SuperiorSkyblockAPI.getIslandAt(e.getPlayer().getLocation());

        if (sIsland == null) {
            e.setDroppedItems(new ArrayList<>());
            e.getPlayer().sendMessage(ChatColor.RED + "The fish got away. You need an island to catch it!");
        } else {
            net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

            int fishingLevel = island.getLevel(IslandProgressionType.FISHING);
            List<FishingLevel> fishUnlocked = new ArrayList<>(fish);

            fishUnlocked.removeIf(x -> fishUnlocked.indexOf(x) > fishingLevel - 1);

            List<FishingLevel> fishPool = new ArrayList<>(fishUnlocked);
            Collections.shuffle(fishPool);

            // Amount generation allows for multiple when the player is multiple levels ahead of the fish they caught
            FishingLevel caughtFish = fishPool.get(0);
            int amount = new Random().nextInt(1,
                    Math.max(2,
                            (int) Math.ceil(fishPool.size() - (double) (fishUnlocked.indexOf(caughtFish) + 1) / 3) + 1));

            ItemStack item = caughtFish.getItem();
            item.setAmount(amount);

            DropEntry dropEntry = new DropEntry(item, 0, caughtFish.getXp());
            dropEntry.setRarity(Rarity.ISLAND);
            e.setDroppedItems(Collections.singletonList(dropEntry));

            api.contributeFishing(e.getPlayer().getUniqueId(), island.getIslandUuid(), caughtFish.getLabel(),
                    item.getAmount());
        }
    }
}
