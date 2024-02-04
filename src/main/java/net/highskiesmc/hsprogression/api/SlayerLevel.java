package net.highskiesmc.hsprogression.api;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.highskiesmc.hscore.configuration.Config;
import net.highskiesmc.hscore.utils.ColorUtils;
import net.highskiesmc.hscore.utils.TextUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SlayerLevel {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private final int level;
    private final EntityType entity;
    private final EntityType previousEntity;
    private final long previousRequired;
    private final int headId;
    private static final HeadDatabaseAPI headApi;

    static {
        headApi = new HeadDatabaseAPI();
    }

    SlayerLevel(int level, EntityType entity, EntityType previousEntity, long previousRequired, int headId) {
        this.level = level;
        this.entity = entity;
        this.previousEntity = previousEntity;
        this.previousRequired = previousRequired;
        this.headId = headId;
    }

    //<editor-fold desc="Getters">
    public int getLevel() {
        return level;
    }

    public EntityType getEntity() {
        return entity;
    }

    public long getPreviousRequired() {
        return previousRequired;
    }

    public int getHeadId() {
        return headId;
    }

    public ItemStack toDisplayItem(@NonNull Island island, Config config) {
        int slayerLevel = island.getLevel(IslandProgressionType.SLAYER);

        ItemStack head = headApi.getItemHead(String.valueOf(headId));
        ItemMeta meta = head.getItemMeta();


        if (slayerLevel >= level) {
            String current = (TextUtils.translateColor(config.get("gui.unlocked.display-name", String.class, "&e&l" +
                            "{name}")
                    .replace("{name}", TextUtils.toTitleCase(getEntity().toString().replace('_', ' ')))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = config.get("gui.slayer.unlocked", ArrayList.class, new ArrayList<>());
            long amount = 0;
            lore.replaceAll(s -> TextUtils.translateColor(s
                            .replace("{amount}", "" + amount)
                            .replace("{current}", current)
                            .replace("{current-no-color}", currentNoColor)
                    )
            );

            meta.setLore(lore);
            head.setItemMeta(meta);
        } else {
            String current = (TextUtils.translateColor(config.get("gui.locked.display-name", String.class, "&7{name}")
                    .replace("{name}", TextUtils.toTitleCase(getEntity().toString().replace('_', ' ')))));
            String currentNoColor = ColorUtils.removeChatColors(current);

            meta.setDisplayName(current);

            List<String> lore = new ArrayList<>(config.get("gui.slayer.locked", ArrayList.class, new ArrayList<>()));
            long amount = 0; // Amount slain
            String previous = TextUtils.toTitleCase(this.previousEntity.toString().replace('_', ' '));

            lore.replaceAll(s -> TextUtils.translateColor(s
                            .replace("{amount}", "" + amount)
                            .replace("{required}", "" + previousRequired)
                            .replace("{current}", current)
                            .replace("{current-no-color}", currentNoColor)
                            .replace("{previous}", previous)
                    )
            );

            meta.setLore(lore);
            head.setItemMeta(meta);
        }

        return head;
    }

    //</editor-fold>
}
