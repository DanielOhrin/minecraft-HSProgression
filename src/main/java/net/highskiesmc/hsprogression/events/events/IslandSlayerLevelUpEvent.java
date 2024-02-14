package net.highskiesmc.hsprogression.events.events;

import net.highskiesmc.hsprogression.api.Island;
import net.highskiesmc.hsprogression.api.SlayerLevel;
import org.bukkit.event.Cancellable;

public class IslandSlayerLevelUpEvent extends IslandEvent implements Cancellable {
    private final SlayerLevel level;
    private boolean isCancelled = false;
    public IslandSlayerLevelUpEvent(Island island, SlayerLevel level) {
        super(island);

        this.level = level;
    }

    /**
     *
     * @return New SlayerLevel
     */
    public SlayerLevel getLevel() {
        return level;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
