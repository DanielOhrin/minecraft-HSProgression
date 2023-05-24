package net.highskiesmc.progression.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedEntity;
import net.highskiesmc.progression.util.ChatColorRemover;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class IsSlayerCommand implements SuperiorCommand {
    private final HSProgressionAPI API;

    public IsSlayerCommand(HSProgressionAPI api) {
        this.API = api;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("slayer");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage(Locale locale) {
        return "slayer";
    }

    @Override
    public String getDescription(Locale locale) {
        return "Shows your island's slayer progression.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public boolean displayCommand() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblock superiorSkyblock, CommandSender sender, String[] args) {
        Player player = (Player) sender;
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
        Island island = superiorPlayer.getIsland();
        if (island == null) {
            player.sendMessage(ChatColor.RED + "You need an island to run this command!");
            return;
        }

        Inventory inv = Bukkit.createInventory(player, 45, ChatColor.translateAlternateColorCodes('&', "&x&0&5&a&f&c" +
                "&6&lI&x&0&c&b&6&c&b&ls&x&1&4&b&d&d&0&ll&x&1&b&c&4&d&5&la&x&2&3&c&b&d&a&ln&x&2&a&d&2&d&f&ld " +
                "&x&3&2&d&a&e&4&lS&x&3&9&e&1&e&9&ll&x&4&1&e&8&e&e&la&x&4&8&e&f&f&3&ly&x&5&0&f&6&f&8&le&x&5&7&f&d&f&d" +
                "&lr"));

        final ConfigurationSection SLAYER_CONFIG = this.API.getConfig(IslandDataType.SLAYER);
        final ConfigurationSection SLAYER_DATA =
                this.API.getIslands().getConfigurationSection(island.getUniqueId().toString() + '.' + IslandDataType.SLAYER.getValue());

        List<ItemStack> trackedItems = new ArrayList<>();
        String previousKey = null;
        boolean previousIsUnlocked = false;
        for (String key : SLAYER_CONFIG.getKeys(false)) {
            if (key.equalsIgnoreCase("lore")) {
                continue;
            }

            final ConfigurationSection ITEM_CONFIG = SLAYER_CONFIG.getConfigurationSection(key);
            final ConfigurationSection ITEM_DATA = SLAYER_DATA.getConfigurationSection(key);

            ItemStack item;
            if (key.equals(TrackedEntity.values()[0].getValue()) || ITEM_DATA.getBoolean("unlocked")) {
                // UNLOCKED ITEM
                item = new HeadDatabaseAPI().getItemHead(ITEM_CONFIG.getString("head-id"));

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ITEM_CONFIG.getString("display-name")));

                List<String> lore = SLAYER_CONFIG.getStringList("lore.unlocked");
                long amount = ITEM_DATA.getLong("amount");
                final String CURRENT = ITEM_CONFIG.getString("display-name");
                final String CURRENT_NO_COLOR = ChatColorRemover.removeChatColors(CURRENT);
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i)
                            .replace("{amount}", "" + amount)
                            .replace("{current}", CURRENT)
                            .replace("{current-no-color}", CURRENT_NO_COLOR);
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                previousIsUnlocked = true;
            } else if (ITEM_DATA.getBoolean("conditions-met")) {
                // CONDITIONS-MET ITEM
                item =
                        new ItemStack(Material.valueOf(this.API.getConfig(null).getString("all.conditions-met" +
                                ".material")));

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        this.API.getConfig(null).getString("all.conditions-met.display-name")
                                .replace("{current}", ITEM_CONFIG.getString("display-name"))
                                .replace("{current-no-color}",
                                        ChatColorRemover.removeChatColors(ITEM_CONFIG.getString("display-name")))));

                List<String> lore = this.API.getConfig(null).getStringList("all.conditions-met.lore");
                double price = ITEM_CONFIG.getDouble("price");
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i)
                            .replace("{price}", "" + price);

                    lore.set(i, ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                previousIsUnlocked = false;
            } else {
                // LOCKED item
                final ConfigurationSection ALL_LOCKED_CONFIG = this.API.getConfig(null).getConfigurationSection("all" +
                        ".locked");
                item = new ItemStack(Material.valueOf(ALL_LOCKED_CONFIG.getString("material")));

                ItemMeta meta = item.getItemMeta();
                List<String> lore = null;

                if (previousIsUnlocked) {
                    item.setType(Material.valueOf(ALL_LOCKED_CONFIG.getString("material-unlockable")));
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            ALL_LOCKED_CONFIG.getString("display-name-unlockable")
                                    .replace("{current}",
                                            ChatColor.translateAlternateColorCodes('&', ITEM_CONFIG.getString(
                                                    "display-name")))
                                    .replace("{current-no-color}",
                                            ChatColorRemover.removeChatColors(ITEM_CONFIG.getString("display-name")))));
                    lore = SLAYER_CONFIG.getStringList("lore.locked");
                } else {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            ALL_LOCKED_CONFIG.getString("display-name")));
                    lore = ALL_LOCKED_CONFIG.getStringList("lore");
                }
                long amount = SLAYER_DATA.getLong(previousKey + ".amount");
                long required = ITEM_CONFIG.getLong("amount");
                double price = ITEM_CONFIG.getDouble("price");
                final String PREVIOUS = SLAYER_CONFIG.getString(previousKey + ".display-name");
                final String PREVIOUS_NO_COLOR = ChatColorRemover.removeChatColors(PREVIOUS);
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i)
                            .replace("{amount}", "" + amount)
                            .replace("{required}", "" + required)
                            .replace("{price}", "" + price)
                            .replace("{previous}", PREVIOUS)
                            .replace("{previous-no-color}", PREVIOUS_NO_COLOR);

                    lore.set(i, ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                previousIsUnlocked = false;
            }


            trackedItems.add(item);
            previousKey = key;
        }

        // Add the tracked items
        List<Integer> slotsToSet = Arrays.asList(9, 18, 27, 28, 29, 20, 11, 12, 13, 22, 31, 32, 33, 24, 15, 16, 17);

        for (int i = 0; i < slotsToSet.size(); i++) {
            inv.setItem(slotsToSet.get(i), trackedItems.get(i));
        }

        // Fill in the rest of the GUI
        ItemStack placeholder =
                new ItemStack(Material.valueOf(this.API.getConfig(null).getString("all.filler.material")));
        ItemMeta meta = placeholder.getItemMeta();
        meta.setDisplayName(" ");
        placeholder.setItemMeta(meta);

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, placeholder);
            }
        }

        player.openInventory(inv);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] strings) {
        return new ArrayList<>();
    }
}
