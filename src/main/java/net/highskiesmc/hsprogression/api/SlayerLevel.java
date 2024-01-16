package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SlayerLevel {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private final int level;
    private final EntityType entity;
    private final long previousRequired;

    SlayerLevel(int level, EntityType entity, long previousRequired) {
        this.level = level;
        this.entity = entity;
        this.previousRequired = previousRequired;
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

    //</editor-fold>
}
