package net.highskiesmc.hsprogression.events.events;

import net.highskiesmc.hsprogression.api.FarmingLevel;
import net.highskiesmc.hsprogression.api.Island;
import org.bukkit.event.Cancellable;

public class IslandFarmingLevelUpEvent extends IslandEvent implements Cancellable {
    private final FarmingLevel level;
    private boolean isCancelled = false;
    public IslandFarmingLevelUpEvent(Island island, FarmingLevel level) {
        super(island);

        this.level = level;
    }

    /**
     *
     * @return New FarmingLevel
     */
    public FarmingLevel getLevel() {
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