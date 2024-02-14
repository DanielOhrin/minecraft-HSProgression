package net.highskiesmc.hsprogression.events.events;

import net.highskiesmc.hsprogression.api.FishingLevel;
import net.highskiesmc.hsprogression.api.Island;
import org.bukkit.event.Cancellable;

public class IslandFishingLevelUpEvent extends IslandEvent implements Cancellable {
    private final FishingLevel level;
    private boolean isCancelled = false;
    public IslandFishingLevelUpEvent(Island island, FishingLevel level) {
        super(island);

        this.level = level;
    }

    /**
     *
     * @return New FishingLevel
     */
    public FishingLevel getLevel() {
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
