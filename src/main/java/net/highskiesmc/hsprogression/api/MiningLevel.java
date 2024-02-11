package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hscore.configuration.Config;
import net.highskiesmc.hscore.utils.ColorUtils;
import net.highskiesmc.hscore.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MiningLevel {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private final int level;
    private final String previousNodeId;
    private final String nodeId;
    private final long previousRequired;

    MiningLevel(int level, String nodeId, String previousNodeId, long previousRequired) {
        this.level = level;
        this.previousNodeId = previousNodeId;
        this.nodeId = nodeId;
        this.previousRequired = previousRequired;
    }

    //<editor-fold desc="Getters">
    public int getLevel() {
        return level;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getPreviousNodeId() {
        return previousNodeId;
    }

    public long getPreviousRequired() {
        return previousRequired;
    }

    // TODO: THIS
  /*  public ItemStack toDisplayItem(@NonNull Island island, Config config) {
        int farmingLevel = island.getLevel(IslandProgressionType.FARMING);

        ItemStack item = new ItemStack(crop);
        ItemMeta meta = item.getItemMeta();


        if (farmingLevel >= level) {
            String current = (TextUtils.translateColor(config.get("gui.unlocked.display-name", String.class, "&e&l" +
                            "{name}")
                    .replace("{name}", TextUtils.toTitleCase(getCrop().toString().replace('_', ' ')))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = new ArrayList<>(config.get("gui.farming.unlocked", ArrayList.class, new ArrayList<>()));
            int amount = island.getFarmingAmount(crop);
            lore.replaceAll(s -> TextUtils.translateColor(s
                            .replace("{amount}", "" + amount)
                            .replace("{current}", current)
                            .replace("{current-no-color}", currentNoColor)
                    )
            );

            meta.setLore(lore);
            item.setItemMeta(meta);
        } else {
            String current = (TextUtils.translateColor(config.get("gui.locked.display-name", String.class, "&7{name}")
                    .replace("{name}", TextUtils.toTitleCase(getCrop().toString().replace('_', ' ')))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = new ArrayList<>(config.get("gui.farming.locked", ArrayList.class, new ArrayList<>()));
            int amount = island.getFarmingAmount(previousCrop); // Amount slain
            String previous = TextUtils.toTitleCase(this.previousCrop.toString().replace('_', ' '));

            int previousAmount = island.getFarmingAmount(previousCrop);
            String progressIndicator = PROGRESS_INCOMPLETE;
            String piHalf = (int) previousRequired / 2 > previousAmount ? PROGRESS_INCOMPLETE : PROGRESS_COMPLETE;
            String piRecipe = PROGRESS_INCOMPLETE; // TODO: recipe tracking update

            lore.replaceAll(s -> TextUtils.translateColor(s
                            .replace("{amount}", "" + amount)
                            .replace("{required}", "" + previousRequired)
                            .replace("{current}", current)
                            .replace("{current-no-color}", currentNoColor)
                            .replace("{previous}", previous)
                            .replace("{progress-indicator}", progressIndicator)
                            .replace("{progress-indicator-half}", piHalf)
                            .replace("{progress-indicator-recipe}", piRecipe)
                            .replace("{recipe}", FarmingRecipe.getRecipe(crop, config).getItemMeta().getDisplayName())
                            .replace("{amount-or-half-required}", String.valueOf(Math.min(previousRequired / 2,
                                    previousAmount)))
                            .replace("{required-half}", String.valueOf(previousRequired / 2))
                    )
            );

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
*/
    //</editor-fold>
}
