package net.highskiesmc.progression.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedNode;
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

public class IsMiningCommand implements SuperiorCommand {
    private final HSProgressionAPI API;

    public IsMiningCommand(HSProgressionAPI api) {
        this.API = api;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("mining");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage(Locale locale) {
        return "mining";
    }

    @Override
    public String getDescription(Locale locale) {
        return "Shows your island's mining progression.";
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

        Inventory inv = Bukkit.createInventory(player, 36, ChatColor.translateAlternateColorCodes('&', "&x&0&5&a&f&c" +
                "&6&lI&x&0&c&b&6&c&b&ls&x&1&4&b&d&d&0&ll&x&1&b&c&4&d&5&la&x&2&3&c&b&d&a&ln&x&2&a&d&2&d&f&ld " +
                "&x&3&2&d&a&e&4&lM&x&3&9&e&1&e&9&li&x&4&1&e&8&e&e&ln&x&4&8&e&f&f&3&li&x&5&0&f&6&f&8&ln&x&5&7&f&d&f&d" +
                "&lg"));

        final ConfigurationSection MINING_CONFIG =
                this.API.getConfig().getConfigurationSection(IslandDataType.MINING.getValue());
        final ConfigurationSection MINING_DATA =
                this.API.getIslands().getConfigurationSection(island.getUniqueId().toString() + '.' + IslandDataType.MINING.getValue());

        List<ItemStack> trackedItems = new ArrayList<>();
        String previousKey = null;
        boolean previousIsUnlocked = false;
        for (String key : MINING_CONFIG.getKeys(false)) {
            if (key.equalsIgnoreCase("lore")) {
                continue;
            }

            final ConfigurationSection ITEM_CONFIG = MINING_CONFIG.getConfigurationSection(key);
            final ConfigurationSection ITEM_DATA = MINING_DATA.getConfigurationSection(key);

            ItemStack item;
            if (key.equals(TrackedNode.values()[0].getValue()) || ITEM_DATA.getBoolean("unlocked")) {
                // UNLOCKED ITEM
                item = new ItemStack(Material.valueOf(ITEM_CONFIG.getString("material")));

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ITEM_CONFIG.getString("display-name")));

                List<String> lore = MINING_CONFIG.getStringList("lore.unlocked");
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i)
                            .replace("{amount}", "" + ITEM_DATA.getLong("amount"))
                            .replace("{current}", ITEM_CONFIG.getString("display-name"))
                            .replace("{current-no-color}", ChatColorRemover.removeChatColors(ITEM_CONFIG.getString(
                                    "display-name")));
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                previousIsUnlocked = true;
            } else if (ITEM_DATA.getBoolean("conditions-met")) {
                // CONDITIONS-MET ITEM
                item = new ItemStack(Material.valueOf(this.API.getConfig().getString("all.conditions-met.material")));

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                this.API.getConfig().getString("all.conditions-met.display-name"))
                        .replace("{current}", ITEM_CONFIG.getString("display-name"))
                        .replace("{current-no-color}",
                                ChatColorRemover.removeChatColors(ITEM_CONFIG.getString("display-name"))));

                List<String> lore = this.API.getConfig().getStringList("all.conditions-met.lore");
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i)
                            .replace("{price}", "" + ITEM_CONFIG.getDouble("price"));

                    lore.set(i, ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                previousIsUnlocked = false;
            } else {
                // LOCKED item
                item = new ItemStack(Material.valueOf(this.API.getConfig().getString("all.locked.material")));

                ItemMeta meta = item.getItemMeta();
                List<String> lore = null;

                if (previousIsUnlocked) {
                    item.setType(Material.valueOf(this.API.getConfig().getString("all.locked.material-unlockable")));
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                    this.API.getConfig().getString("all.locked.display-name-unlockable"))
                            .replace("{current}", ChatColor.translateAlternateColorCodes('&', ITEM_CONFIG.getString(
                                    "display-name")))
                            .replace("{current-no-color}",
                                    ChatColorRemover.removeChatColors(ITEM_CONFIG.getString("display-name"))));
                    lore = MINING_CONFIG.getStringList("lore.locked");
                } else {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            this.API.getConfig().getString("all.locked.display-name")));
                    lore = this.API.getConfig().getStringList("all.locked.lore");
                }
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i)
                            .replace("{amount}", "" + MINING_DATA.getLong(previousKey + ".amount"))
                            .replace("{required}", "" + ITEM_CONFIG.getLong("amount"))
                            .replace("{price}", "" + ITEM_CONFIG.getDouble("price"))
                            .replace("{previous}", MINING_CONFIG.getString(previousKey + ".display-name"))
                            .replace("{previous-no-color}", ChatColorRemover.removeChatColors(MINING_CONFIG.getString(
                                    previousKey + ".display-name")));
                    ;

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
        List<Integer> slotsToSet = Arrays.asList(10, 19, 20, 21, 12, 13, 14, 23, 24, 25, 16);

        for (int i = 0; i < slotsToSet.size(); i++) {
            inv.setItem(slotsToSet.get(i), trackedItems.get(i));
        }

        // Fill in the rest of the GUI
        ItemStack placeholder =
                new ItemStack(Material.valueOf(this.API.getConfig().getString("all.filler.material")));
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
