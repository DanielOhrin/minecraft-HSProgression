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

public class FarmingLevel implements DisplayableItem {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private final int level;
    private final Material previousCrop;
    private final Material crop;
    private final Material seed;
    private final long previousRequired;

    FarmingLevel(int level, Material crop, Material seed, Material previousCrop, long previousRequired) {
        this.level = level;
        this.crop = crop;
        this.seed = seed;
        this.previousCrop = previousCrop;
        this.previousRequired = previousRequired;
    }

    //<editor-fold desc="Getters">
    public int getLevel() {
        return level;
    }

    public Material getCrop() {
        return crop;
    }
    public Material getSeed() {
        return seed;
    }

    public long getPreviousRequired() {
        return previousRequired;
    }

    public ItemStack toDisplayItem(@NonNull Island island, Config config) {
        int slayerLevel = island.getLevel(IslandProgressionType.FARMING);

        ItemStack item = new ItemStack(crop);
        ItemMeta meta = item.getItemMeta();


        if (slayerLevel >= level) {
            String current = (TextUtils.translateColor(config.get("gui.unlocked.display-name", String.class, "&e&l" +
                            "{name}")
                    .replace("{name}", TextUtils.toTitleCase(getCrop().toString().replace('_', ' ')))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = new ArrayList<>(config.get("gui.slayer.unlocked", ArrayList.class, new ArrayList<>()));
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

            List<String> lore = new ArrayList<>(config.get("gui.slayer.locked", ArrayList.class, new ArrayList<>()));
            int amount = island.getFarmingAmount(previousCrop); // Amount slain
            String previous = TextUtils.toTitleCase(this.previousCrop.toString().replace('_', ' '));

            lore.replaceAll(s -> TextUtils.translateColor(s
                            .replace("{amount}", "" + amount)
                            .replace("{required}", "" + previousRequired)
                            .replace("{current}", current)
                            .replace("{current-no-color}", currentNoColor)
                            .replace("{previous}", previous)
                    )
            );

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    //</editor-fold>
}
