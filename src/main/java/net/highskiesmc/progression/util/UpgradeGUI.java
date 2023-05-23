package net.highskiesmc.progression.util;

import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
import net.highskiesmc.progression.enums.TrackedEntity;
import net.highskiesmc.progression.enums.TrackedNode;
import org.bukkit.inventory.Inventory;

public class UpgradeGUI {
    private final Inventory INVENTORY;
    private final double AMOUNT;
    private final IslandDataType DATA_TYPE;
    private final TrackedNode TRACKED_NODE;
    private final TrackedCrop TRACKED_CROP;
    private final TrackedEntity TRACKED_ENTITY;

    public UpgradeGUI(Inventory inventory, double amount, IslandDataType dataType, TrackedNode node, TrackedCrop crop
            , TrackedEntity entity) {
        this.INVENTORY = inventory;
        this.AMOUNT = amount;
        this.DATA_TYPE = dataType;
        this.TRACKED_NODE = node;
        this.TRACKED_CROP = crop;
        this.TRACKED_ENTITY = entity;
    }

    public Inventory getInventory() {
        return this.INVENTORY;
    }

    public String getDataKey() {
        switch (this.DATA_TYPE) {
            case SLAYER:
                return this.TRACKED_ENTITY.getValue();
            case MINING:
                return this.TRACKED_NODE.getValue();
            case FARMING:
                return this.TRACKED_CROP.getValue();
            default:
                return null;
        }
    }

    public double getAmount() {
        return this.AMOUNT;
    }

    public IslandDataType getDataType() {
        return this.DATA_TYPE;
    }
}
