package net.highskiesmc.hsprogression.events.events;

import net.highskiesmc.hsprogression.api.Island;
import net.highskiesmc.hsprogression.api.IslandProgressionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IslandContributionEvent extends IslandEvent implements Cancellable {
    private boolean cancel = false;
    private final IslandProgressionType progressionType;
    private final Player player;
    private int amount;
    public IslandContributionEvent(@NonNull Island island,
                                   @NonNull Player player,
                                   @NonNull IslandProgressionType progressionType,
                                   int amount) {
        super(island);

        this.progressionType = progressionType;
        this.player = player;
        this.amount = amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Player getPlayer() {
        return player;
    }

    public IslandProgressionType getProgressionType() {
        return progressionType;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
