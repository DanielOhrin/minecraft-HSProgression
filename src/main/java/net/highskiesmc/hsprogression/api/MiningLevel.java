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

public class MiningLevel implements DisplayableItem {
    private static final String PROGRESS_COMPLETE = TextUtils.translateColor("&f[&a✓&f]");
    private static final String PROGRESS_INCOMPLETE = TextUtils.translateColor("&f[&cx&f]");
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private final int level;
    private final String previousNodeId;
    private final String nodeId;
    private final Material material;
    private final long previousRequired;

    MiningLevel(int level, Material material, String nodeId, String previousNodeId, long previousRequired) {
        this.level = level;
        this.material = material;
        this.previousNodeId = previousNodeId;
        this.nodeId = nodeId;
        this.previousRequired = previousRequired;
    }

    //<editor-fold desc="Getters">
    public int getLevel() {
        return level;
    }

    public Material getMaterial() {
        return material;
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

   public ItemStack toDisplayItem(@NonNull Island island, Config config) {
        int miningLevel = island.getLevel(IslandProgressionType.MINING);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();


        if (miningLevel >= level) {
            String current = (TextUtils.translateColor(config.get("gui.unlocked.display-name", String.class, "&e&l" +
                            "{name}")
                    .replace("{name}", TextUtils.toTitleCase(getNodeId().toString().replace('-', ' ')))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = new ArrayList<>(config.get("gui.mining.unlocked", ArrayList.class, new ArrayList<>()));
            int amount = island.getMiningAmount(nodeId);
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
                    .replace("{name}", TextUtils.toTitleCase(getNodeId().toString().replace('-', ' ')))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = new ArrayList<>(config.get("gui.mining.locked", ArrayList.class, new ArrayList<>()));
            int amount = island.getMiningAmount(previousNodeId); // Amount slain
            String previous = TextUtils.toTitleCase(this.previousNodeId.toString().replace('-', ' '));

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
