package net.highskiesmc.hsprogression.api;

import dev.lone.itemsadder.api.CustomStack;
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

public class FishingLevel implements DisplayableItem {
    private static final String PROGRESS_COMPLETE = TextUtils.translateColor("&f[&a✓&f]");
    private static final String PROGRESS_INCOMPLETE = TextUtils.translateColor("&f[&cx&f]");
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private final int level;
    private final String previousId;
    private final String previousLabel;
    private final String label;
    private final String id;
    private final long previousRequired;
    private final double xp;

    FishingLevel(int level, String previousId, String previousLabel, String label, String id, double xp,
                 long previousRequired) {
        this.level = level;
        this.previousId = previousId;
        this.previousLabel = previousLabel;
        this.label = label;
        this.id = id;
        this.xp = xp;
        this.previousRequired = previousRequired;
    }

    //<editor-fold desc="Getters">
    public int getLevel() {
        return level;
    }

    public double getXp() {
        return xp;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getPreviousLabel() {
        return previousLabel;
    }

    public ItemStack getItem() {
        Material mat = Material.getMaterial(id);

        return mat == null ? CustomStack.getInstance(id).getItemStack() : new ItemStack(mat);
    }
    // TODO: track player BREAKING/HARVESTING crops for leaderboards

    public long getPreviousRequired() {
        return previousRequired;
    }

    public ItemStack toDisplayItem(@NonNull Island island, Config config) {
        int farmingLevel = island.getLevel(IslandProgressionType.FISHING);

        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();


        if (farmingLevel >= level) {
            String current = (TextUtils.translateColor(config.get("gui.unlocked.display-name", String.class, "&e&l" +
                            "{name}")
                    .replace("{name}", TextUtils.toTitleCase(getLabel()))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = new ArrayList<>(config.get("gui.fishing.unlocked", ArrayList.class, new ArrayList<>()));
            int amount = island.getFishingAmount(id);
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
                    .replace("{name}", TextUtils.toTitleCase(getLabel()))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = new ArrayList<>(config.get("gui.fishing.locked", ArrayList.class, new ArrayList<>()));
            int amount = island.getFishingAmount(previousId);
            String previous = TextUtils.toTitleCase(this.getPreviousLabel());

            lore.replaceAll(s -> TextUtils.translateColor(s
                            .replace("{amount}", "" + amount)
                            .replace("{required}", "" + previousRequired)
                            .replace("{current}", current)
                            .replace("{current-no-color}", currentNoColor)
                            .replace("{previous}", previous)
                            .replace("{progress-indicator}", PROGRESS_INCOMPLETE)
                    )
            );

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    //</editor-fold>
}
