package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import dev.rosewood.rosestacker.event.SpawnerStackEvent;
import dev.rosewood.rosestacker.stack.StackedSpawner;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.LocationUtils;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.api.IslandProgressionType;
import net.highskiesmc.hsprogression.api.SlayerLevel;
import net.highskiesmc.hsprogression.events.events.IslandContributionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;
import java.util.stream.Collectors;

public class IslandSlayerEventsHandler extends HSListener {
    private final HSProgressionApi api;
    private final Map<EntityType, Integer> trackedEntityTypes;

    public IslandSlayerEventsHandler(HSPlugin main, HSProgressionApi api) {
        super(main);
        this.api = api;

        this.trackedEntityTypes = api.getSlayerLevels().stream().collect(Collectors.toMap(SlayerLevel::getEntity,
                SlayerLevel::getLevel));
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerStackChange(SpawnerStackEvent e) {
        StackedSpawner spawner = e.getStack();

        Island sIsland = SuperiorSkyblockAPI.getIslandAt(spawner.getLocation());

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null) {
            return;
        }

        if (!trackedEntityTypes.containsKey(spawner.getSpawner().getSpawnedType())) {
            main.getLogger().warning(e.getPlayer().getName() + " attempted to place " + e.getIncreaseAmount() + spawner.getSpawner().getSpawnedType()
                    + " spawner at " + LocationUtils.serializeLocation(spawner.getLocation(), true));
            e.setCancelled(true);
            return;
        }

        if (island.getLevel(IslandProgressionType.SLAYER) < trackedEntityTypes.get(spawner.getSpawner().getSpawnedType())) {
            e.setCancelled(true);

            e.getPlayer().sendMessage(TextUtils.translateColor(config.get("island.not-unlocked",
                            String.class,
                            "&4&l[!]&c Island has not unlocked that! &cReason: &f{reason}"
                    ).replace("{reason}", "Insufficient slayer level.")
            ));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntitySlainOnIsland(EntityDeathEvent e) {
        EntityType type = e.getEntityType();
        Entity entity = e.getEntity();
        Player slayer = e.getEntity().getKiller();

        Island sIsland = SuperiorSkyblockAPI.getIslandAt(entity.getLocation());

        if (sIsland == null) {
            return;
        }

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);

        if (island == null || slayer == null) {
            return;
        }

        if (trackedEntityTypes.containsKey(type) && island.getLevel(IslandProgressionType.SLAYER) >= trackedEntityTypes.get(type)) {
            IslandContributionEvent event = new IslandContributionEvent(island, slayer, IslandProgressionType.SLAYER,
                    1);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                api.contributeSlayer(slayer.getUniqueId(), island.getIslandUuid(), type, event.getAmount());
            }
        }
    }
}
