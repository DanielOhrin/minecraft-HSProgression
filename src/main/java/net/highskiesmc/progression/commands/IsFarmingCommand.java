package net.highskiesmc.progression.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
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

public class IsFarmingCommand implements SuperiorCommand {
    private final HSProgressionAPI API;

    public IsFarmingCommand(HSProgressionAPI api) {
        this.API = api;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("farming");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage(Locale locale) {
        return "farming";
    }

    @Override
    public String getDescription(Locale locale) {
        return "Shows your island's farming progression.";
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
                "&6&lI&x&0&c&b&6&c&b&ls&x&1&3&b&c&c&f&ll&x&1&a&c&3&d&4&la&x&2&0&c&9&d&8&ln&x&2&7&d&0&d&d&ld " +
                "&x&2&e&d&6&e&2&lF&x&3&5&d&d&e&6&la&x&3&c&e&3&e&b&lr&x&4&3&e&a&e&f&lm&x&4&9&f&0&f&4&li&x&5&0&f&7&f&8" +
                "&ln&x&5&7&f&d&f&d&lg"));

        final ConfigurationSection FARMING_CONFIG = this.API.getConfig(IslandDataType.FARMING);
        final ConfigurationSection FARMING_DATA =
                this.API.getIslands().getConfigurationSection(island.getUniqueId().toString() + '.' + IslandDataType.FARMING.getValue());

        List<ItemStack> trackedItems = new ArrayList<>();
        String previousKey = null;
        boolean previousIsUnlocked = false;
        for (String key : FARMING_CONFIG.getKeys(false)) {
            if (key.equalsIgnoreCase("lore") || key.equalsIgnoreCase("recipe")) {
                continue;
            }

            final ConfigurationSection ITEM_CONFIG = FARMING_CONFIG.getConfigurationSection(key);
            final ConfigurationSection ITEM_DATA = FARMING_DATA.getConfigurationSection(key);

            ItemStack item;
            if (key.equals(TrackedCrop.values()[0].getValue()) || ITEM_DATA.getBoolean("unlocked")) {
                // UNLOCKED ITEM
                item = new ItemStack(Material.valueOf(ITEM_CONFIG.getString("material")));

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ITEM_CONFIG.getString("display-name")));

                List<String> lore = FARMING_CONFIG.getStringList("lore.unlocked");
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

                final String CURRENT = ITEM_CONFIG.getString("display-name");
                final String CURRENT_NO_COLOR = ChatColorRemover.removeChatColors(CURRENT);

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        this.API.getConfig(null).getString("all.conditions-met.display-name")
                                .replace("{current}", CURRENT)
                                .replace("{current-no-color}", CURRENT_NO_COLOR)));

                List<String> lore = this.API.getConfig(null).getStringList("all.conditions-met.lore");
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);

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
                                    ALL_LOCKED_CONFIG.getString("display-name-unlockable"))
                            .replace("{current}", ChatColor.translateAlternateColorCodes('&', ITEM_CONFIG.getString(
                                    "display-name")))
                            .replace("{current-no-color}",
                                    ChatColorRemover.removeChatColors(ITEM_CONFIG.getString("display-name"))));
                    lore = FARMING_CONFIG.getStringList("lore.locked");
                } else {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            ALL_LOCKED_CONFIG.getString("display-name")));
                    lore = ALL_LOCKED_CONFIG.getStringList("lore");
                }

                long amount = FARMING_DATA.getLong(previousKey + ".amount");
                long required = ITEM_CONFIG.getLong("amount");
                long requiredHalf = required / 2;
                long amountOrHalfRequired = Math.min(amount, requiredHalf);
                final String PREVIOUS = FARMING_CONFIG.getString(previousKey + ".display-name");
                final String PREVIOUS_NO_COLOR = ChatColorRemover.removeChatColors(PREVIOUS);
                final String PROGRESS_INDICATOR = amount >= requiredHalf ?
                        ChatColor.translateAlternateColorCodes('&'
                        , "&f[&a✓&f]") :
                        ChatColor.translateAlternateColorCodes('&', "&f[&cx&f]");
                final String RECIPE =
                        this.API.getFullRecipe(Arrays.stream(TrackedCrop.values()).filter(c -> c.getValue().equals(key)).findFirst().get()).getItemMeta().getDisplayName();
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i)
                            .replace("{amount}", "" + amount)
                            .replace("{amount-or-half-required}", "" + amountOrHalfRequired)
                            .replace("{required}", "" + required)
                            .replace("{required-half}", "" + requiredHalf)
                            .replace("{previous}", PREVIOUS)
                            .replace("{previous-no-color}", PREVIOUS_NO_COLOR)
                            .replace("{progress-indicator}", PROGRESS_INDICATOR)
                            .replace("{recipe}", RECIPE);

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
        List<Integer> slotsToSet = Arrays.asList(10, 19, 28, 29, 30, 21, 12, 13, 14, 23, 32, 33, 34, 25);

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
