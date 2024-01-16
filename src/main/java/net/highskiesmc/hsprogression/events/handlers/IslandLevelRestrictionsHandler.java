package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandInviteEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandJoinEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.key.Key;
import dev.rosewood.rosestacker.event.SpawnerStackEvent;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.api.IslandProgressionType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.stream.Collectors;

public class IslandLevelRestrictionsHandler extends HSListener {
    private final HSProgressionApi api;

    public IslandLevelRestrictionsHandler(HSPlugin main, HSProgressionApi api) {
        super(main);
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlaceOnIsland(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        Island sIsland = SuperiorSkyblockAPI.getIslandAt(block.getLocation());

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null) {
            return;
        }

        if (!api.canPlace(island, e.getItemInHand())) {
            e.setCancelled(true);

            e.getPlayer().sendMessage(TextUtils.translateColor(
                    config.get(
                            "island.not-unlocked",
                            String.class,
                            "&4&l[!]&c Island has not unlocked that! &cReason: &f{reason}"
                    ).replace("{reason}", "Level too low")
            ));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInviteToIsland(IslandInviteEvent e) {
        Island sIsland = e.getIsland();

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null) {
            return;
        }

        int members = sIsland.getIslandMembers(true).size();
        int memberLimit = api.getIslandLevel(island.getLevel(IslandProgressionType.ISLAND)).getMaxMembers();

        if (memberLimit == members) {
            e.setCancelled(true);
            e.getPlayer().asPlayer().sendMessage(TextUtils.translateColor(
                    config.get("island.member-limit", String.class, "&4&l[!]&c Island has reached its member limit.")
            ));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onJoinIsland(IslandJoinEvent e) {
        Island sIsland = e.getIsland();

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null) {
            return;
        }

        int members = sIsland.getIslandMembers(true).size();
        int memberLimit = api.getIslandLevel(island.getLevel(IslandProgressionType.ISLAND)).getMaxMembers();

        if (memberLimit == members) {
            e.setCancelled(true);
            e.getPlayer().asPlayer().sendMessage(TextUtils.translateColor(
                    config.get("island.member-limit", String.class, "&4&l[!]&c Island has reached its member limit.")
            ));
        }
        // TOTO: Add max spawner restrictions
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlaceSpawnerOnIsland(SpawnerStackEvent e) {
        int increaseAmount = e.getIncreaseAmount();

        Island sIsland = SuperiorSkyblockAPI.getIslandAt(e.getStack().getLocation());

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null) {
            return;
        }

        int amountPlaced =
                sIsland.getBlocksTracker().getBlockCounts().entrySet().stream().filter(x -> x.getKey().getGlobalKey().toLowerCase().contains("spawner")).mapToInt(x -> x.getValue().intValue()).sum();
        int spawnerLimit = api.getIslandLevel(island.getLevel(IslandProgressionType.ISLAND)).getMaxSpawners();

        String feedback = TextUtils.translateColor(
                config.get("island.spawner-limit",
                                String.class,
                                "&4&l[!]&c {owner}'s Island cannot hold that many spawners. &c{amount}/{max}"
                        )
                        .replace("{owner}", sIsland.getOwner().getName())
                        .replace("{max}", String.valueOf(spawnerLimit))
        );

        String feedbackReached = TextUtils.translateColor(
                config.get("island.spawner-limit-reached",
                                String.class,
                                "&4&l[!]&c {owner}'s Island has reached its max spawners. &c{amount}/{max}"
                        )
                        .replace("{owner}", sIsland.getOwner().getName())
                        .replace("{max}", String.valueOf(spawnerLimit))
        );

        if (amountPlaced == spawnerLimit) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(feedback.replace("{amount}", String.valueOf(amountPlaced)));
        } else if (amountPlaced + increaseAmount > spawnerLimit) {
            int newIncreaseAmount = spawnerLimit - amountPlaced;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "rs give spawner " + e.getPlayer().getName() + " " + e.getStack().getSpawner().getSpawnedType() + " " + (increaseAmount - newIncreaseAmount));
            main.getLogger().info("Refunded overflow spawners to " + e.getPlayer().getName());
            e.setIncreaseAmount(newIncreaseAmount);
            e.getPlayer().sendMessage(feedbackReached.replace("{amount}", String.valueOf(spawnerLimit)));
        }
    }
}
