package net.highskiesmc.hsprogression.inventories;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.hscore.inventory.GUI;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.api.Island;
import net.highskiesmc.hsprogression.api.IslandLevel;
import net.highskiesmc.hsprogression.api.IslandProgressionType;
import net.highskiesmc.hsprogression.events.events.IslandLevelUpEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.List;

public class IslandLevelsGUI implements GUI {
    private final HSProgression main;
    private final List<IslandLevel> levels;
    private final HSProgressionApi api;
    private final Island island;
    private int level;
    private final Player player;

    public IslandLevelsGUI(HSProgression main, Island island, Player player) {
        this.main = main;
        this.api = HSProgression.getApi();
        this.island = island;
        this.level = island.getLevel(IslandProgressionType.ISLAND);
        this.levels = api.getIslandLevels();
        this.player = player;
    }

    public IslandLevelsGUI(HSProgression main, com.bgsoftware.superiorskyblock.api.island.Island island,
                           Player player) {
        this.main = main;
        this.api = HSProgression.getApi();
        this.island = api.getIsland(island);
        this.level = this.island.getLevel(IslandProgressionType.ISLAND);
        this.levels = api.getIslandLevels();
        this.player = player;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        int slot = e.getRawSlot();

        // If slot clicked is next unlock-able level
        if (slot != 0 && slot < levels.size() && slot == level) {
            Economy econ = HSProgression.getEconomy();

            // If player has enough money
            long cost = levels.get(level).getCost();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            if (econ.getBalance(offlinePlayer) < cost) {
                player.closeInventory();
                player.sendMessage(TextUtils.translateColor(
                        main.getConfigs().get("common.need-money", String.class, "&cInsufficient funds.")
                ));
                return;
            }

            IslandLevelUpEvent event = new IslandLevelUpEvent(island, player, levels.get(level));

            if (!event.isCancelled()) {
                // Upgrade logic
                econ.withdrawPlayer(offlinePlayer, cost);
                level++;
                api.setIslandLevel(island, level);
                SuperiorSkyblockAPI.getIslandByUUID(island.getIslandUuid()).setIslandSize(
                        api.getIslandLevel(level).getIslandRadius()
                );
                // Cosmetics and post-processing
                addContent(e.getInventory()); // <-- Updates inventory
                if (levels.get(level - 1).isAnnounced()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(TextUtils.translateColor(
                                main.getConfigs().get(
                                                "island.upgraded",
                                                String.class,
                                                "&4&l[!]&c {owner}'s Island has been upgraded to level &f&l{level}!"
                                        ).replace("{owner}",
                                                Bukkit.getOfflinePlayer(island.getLeaderUuid()).getName())
                                        .replace("{level}", String.valueOf(level))
                        ));
                    }
                } else {
                    for (SuperiorPlayer player :
                            SuperiorSkyblockAPI.getIslandByUUID(island.getIslandUuid()).getIslandMembers(true)) {
                        if (player.isOnline()) {
                            player.asPlayer().sendMessage(TextUtils.translateColor(
                                    main.getConfigs().get(
                                                    "island.upgraded",
                                                    String.class,
                                                    "&4&l[!]&c {owner}'s Island has been upgraded to level &f&l{level}!"
                                            ).replace("{owner}",
                                                    Bukkit.getOfflinePlayer(island.getLeaderUuid()).getName())
                                            .replace("{level}", String.valueOf(level))
                            ));
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

            Economy econ = HSProgression.getEconomy();
            inv.setItem(i, level.toDisplayItem(econ.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())),
                    this.level));
        }
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(
                this,
                18,
                TextUtils.translateColor(
                        main.getConfigs().get("island-levels-menu-title", String.class, "&x&0&8&4&c&f&bI&x&1&5&5&9&f" +
                                "&bs&x&2&1&6&6&f&bl&x&2&e&7&3&f&ba&x&3&b&7&f&f&cn&x&4&7&8&c&f&cd " +
                                "&x&5&4&9&9&f&cU&x&6&1&a&6&f&cp&x&6&e&b&3&f&cg&x&7&a&c&0&f&cr&x&8&7&c&c&f&da&x&9&4&d" +
                                "&9&f&dd&x&a&0&e&6&f&de&x&a&d&f&3&f&ds"
                ))
        );

        addContent(inv);

        return inv;
    }
}
