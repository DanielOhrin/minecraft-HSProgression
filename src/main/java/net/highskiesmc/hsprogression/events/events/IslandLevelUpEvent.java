package net.highskiesmc.hsprogression.events.events;

import net.highskiesmc.hsprogression.api.Island;
import net.highskiesmc.hsprogression.api.IslandLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IslandLevelUpEvent extends IslandEvent implements Cancellable {
    private boolean isCancelled;
    private final Player player;
    private final IslandLevel level;
    public IslandLevelUpEvent(@NonNull Island island, @NonNull Player player, IslandLevel level) {
        super(island);
        this.isCancelled = false;
        this.player = player;
        this.level = level;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    /**
     *
     * @return Player responsible for upgrading the island
     */
    public Player getPlayer() {
        return this.player;
    }

    public IslandLevel getNewLevel() {
        return this.level;
    }
}
