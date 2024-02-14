package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hscore.configuration.Config;
import org.bukkit.inventory.ItemStack;

public interface DisplayableItem {
    ItemStack toDisplayItem(Island island, Config config);
}
