package net.highskiesmc.hsprogression.events.handlers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.highskiesmc.hscore.highskies.HSListener;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.api.*;
import net.highskiesmc.hsprogression.events.events.IslandContributionEvent;
import net.highskiesmc.nodes.events.events.IslandNodeMineEvent;
import net.highskiesmc.nodes.events.events.IslandNodePlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Objects;

public class IslandMiningEventsHandler extends HSListener {
    private final HSProgressionApi api;
    private final List<String> trackedNodes;

    public IslandMiningEventsHandler(HSPlugin main, HSProgressionApi api) {
        super(main);

        this.api = api;
        this.trackedNodes = api.getMiningLevels().stream().map(MiningLevel::getNodeId).toList();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNodeMineOnIsland(IslandNodeMineEvent e) {
        Island island = api.getIsland(e.getIsland());

        if (island == null) {
            return;
        }

        if (e.getIsland().getIslandMembers(true).contains(SuperiorSkyblockAPI.getPlayer(e.getPlayer()))) {
            IslandContributionEvent event = new IslandContributionEvent(island, e.getPlayer(),
                    IslandProgressionType.MINING, 1);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                api.contributeMining(e.getPlayer().getUniqueId(), island.getIslandUuid(), e.getNode().getType(),
                        event.getAmount());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNodePlaceOnIsland(IslandNodePlaceEvent e) {
        Island island = api.getIsland(e.getIsland());

        if (island == null) {
            return;
        }

        String nodeId = e.getNode().getType();

        if (!Objects.equals(nodeId, trackedNodes.get(0))) {
            int requiredMiningLevel = trackedNodes.indexOf(nodeId) + 1;

            if (island.getLevel(IslandProgressionType.MINING) < requiredMiningLevel) {
                e.setCancelled(true);

                e.getPlayer().sendMessage(TextUtils.translateColor(
                        config.get("island.not-unlocked", String.class, "&4&l[!]&c Island has not unlocked " +
                                        "that! &cReason: &f{reason}")
                                .replace("{reason}", "Insufficient mining level")
                ));
            }
        }
    }
}
