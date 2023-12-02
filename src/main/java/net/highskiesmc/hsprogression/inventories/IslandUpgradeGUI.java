package net.highskiesmc.hsprogression.inventories;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.hscore.inventory.GUI;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.api.Island;
import net.highskiesmc.hsprogression.api.IslandLevel;
import net.highskiesmc.hsprogression.events.events.IslandUpgradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.List;

public class IslandUpgradeGUI implements GUI {
    // TODO: Make this a config.yml value
    private static final String TITLE = ChatColor.translateAlternateColorCodes('&', "&x&0&8&4&c&f&bI&x&1&5&5&9&f" +
            "&bs&x&2&1&6&6&f&bl&x&2&e&7&3&f&ba&x&3&b&7&f&f&cn&x&4&7&8&c&f&cd " +
            "&x&5&4&9&9&f&cU&x&6&1&a&6&f&cp&x&6&e&b&3&f&cg&x&7&a&c&0&f&cr&x&8&7&c&c&f&da&x&9&4&d&9&f&dd&x&a&0&e&6&f" +
            "&de&x&a&d&f&3&f&ds");
    private final List<IslandLevel> levels;
    private final HSProgressionApi api;
    private final Island island;
    private int level;
    private final Player player;

    public IslandUpgradeGUI(Island island, Player player) {
        this.api = HSProgression.getApi();
        this.island = island;
        this.level = island.getLevel();
        this.levels = api.getIslandLevels();
        this.player = player;
    }

    public IslandUpgradeGUI(com.bgsoftware.superiorskyblock.api.island.Island island, Player player) {
        this.api = HSProgression.getApi();
        this.island = api.getIsland(island);
        this.level = this.island.getLevel();
        this.levels = api.getIslandLevels();
        this.player = player;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        int slot = e.getRawSlot();

        // If slot clicked is next unlock-able level
        if (slot != 0 && slot < levels.size() && slot == level) {
            //TODO: Take money here with a confirmation menu
            IslandUpgradeEvent event = new IslandUpgradeEvent(island, player, levels.get(level));

            if (!event.isCancelled()) {
                // Upgrade logic
                level++;
                api.setIslandLevel(island, level);
                SuperiorSkyblockAPI.getIslandByUUID(island.getIslandUuid()).setIslandSize(
                        api.getIslandLevel(level).getIslandRadius()
                );
                // Cosmetics and post-processing
                addContent(e.getInventory()); // <-- Updates inventory
                if (levels.get(level - 1).isAnnounced()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(Bukkit.getOfflinePlayer(this.island.getLeaderUuid()).getName() + "'s " +
                                "island has reached level " + level);
                    }
                } else {
                    for (SuperiorPlayer player :
                            SuperiorSkyblockAPI.getIslandByUUID(island.getIslandUuid()).getIslandMembers(true)) {
                        if (player.isOnline()) {
                            player.asPlayer().sendMessage("Congrats you upgraded your island...");
                            // TODO: Update to configurable value
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onInventoryOpen(InventoryOpenEvent e) {

    }

    @Override
    public void onInventoryClose(InventoryCloseEvent e) {

    }

    @Override
    public void addContent(Inventory inv) {
        inv.clear();

        List<IslandLevel> levels = HSProgression.getApi().getIslandLevels();

        for (int i = 0; i < levels.size(); i++) {
            IslandLevel level = levels.get(i);

            inv.setItem(i, level.toDisplayItem(this.level));
        }
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 18, TITLE);

        addContent(inv);

        return inv;
    }
}
