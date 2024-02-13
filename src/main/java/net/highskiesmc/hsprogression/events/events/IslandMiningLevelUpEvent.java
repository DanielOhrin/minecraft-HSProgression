package net.highskiesmc.hsprogression.events.events;

import net.highskiesmc.hsprogression.api.Island;
import net.highskiesmc.hsprogression.api.MiningLevel;
import org.bukkit.event.Cancellable;

public class IslandMiningLevelUpEvent extends IslandEvent implements Cancellable {
    private final MiningLevel level;
    private boolean isCancelled = false;
    public IslandMiningLevelUpEvent(Island island, MiningLevel level) {
        super(island);

        this.level = level;
    }

    /**
     *
     * @return New MiningLevel
     */
    public MiningLevel getLevel() {
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
