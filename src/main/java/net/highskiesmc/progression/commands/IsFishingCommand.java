package net.highskiesmc.progression.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import dev.lone.itemsadder.api.CustomStack;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.IslandFishingBuff;
import net.highskiesmc.progression.enums.TrackedFish;
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

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class IsFishingCommand implements SuperiorCommand {
    private final HSProgressionAPI API;

    public IsFishingCommand(HSProgressionAPI api) {
        this.API = api;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("fishing");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage(Locale locale) {
        return "fishing";
    }

    @Override
    public String getDescription(Locale locale) {
        return "Shows your island's fishing progression.";
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

        Inventory inv = Bukkit.createInventory(player, 27, IslandDataType.FISHING.getGUITitle());

        final ConfigurationSection FISHING_CONFIG = this.API.getConfig(IslandDataType.FISHING);
        final ConfigurationSection FISHING_DATA =
                this.API.getIslands().getConfigurationSection(island.getUniqueId().toString() + '.' + IslandDataType.FISHING.getValue());

        List<ItemStack> trackedItems = new ArrayList<>();
        String previousKey = null;
        boolean previousIsUnlocked = false;
        for (String key : FISHING_CONFIG.getKeys(false)) {
            if (key.equalsIgnoreCase("lore")) {
                continue;
            }

            final ConfigurationSection ITEM_CONFIG = FISHING_CONFIG.getConfigurationSection(key);
            final ConfigurationSection ITEM_DATA = FISHING_DATA.getConfigurationSection(key);

            ItemStack item;
            if (key.equals(TrackedFish.values()[0].getValue()) || ITEM_DATA.getBoolean("unlocked")) {
                // UNLOCKED ITEM
                String material = ITEM_CONFIG.getString("material");
                if (material != null) {
                    item = new ItemStack(Material.valueOf(material));
                } else {
                    item = CustomStack.getInstance(ITEM_CONFIG.getString("namespaced-id")).getItemStack();
                }

                ItemMeta meta = item.getItemMeta();
                final String CURRENT = ITEM_CONFIG.getString("display-name");
                final String CURRENT_NO_COLOR = ChatColorRemover.removeChatColors(CURRENT);
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        ITEM_CONFIG.getString("display-name")
                                .replace("{current}", CURRENT)
                                .replace("{current-no-color}", CURRENT_NO_COLOR)
                ));

                List<String> lore = FISHING_CONFIG.getStringList("lore.unlocked");
                long amount = ITEM_DATA.getLong("amount");
                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{current}", CURRENT)
                        .replace("{current-no-color}", CURRENT_NO_COLOR)));
                meta.setLore(lore);
                item.setItemMeta(meta);

                previousIsUnlocked = true;
            } else if (ITEM_DATA.getBoolean("conditions-met")) {
                // CONDITIONS-MET ITEM
                item =
                        new ItemStack(Material.valueOf(this.API.getConfig(null).getString("all.conditions-met" +
                                ".material")));

                ItemMeta meta = item.getItemMeta();
                final String CURRENT = ITEM_CONFIG.getString("display-name");
                final String CURRENT_NO_COLOR = ChatColorRemover.removeChatColors(CURRENT);
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        this.API.getConfig(null).getString("all.conditions-met.display-name")
                                .replace("{current}", CURRENT)
                                .replace("{current-no-color}", CURRENT_NO_COLOR)));

                List<String> lore = this.API.getConfig(null).getStringList("all.conditions-met.lore");
                double price = ITEM_CONFIG.getDouble("price");
                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s
                        .replace("{price}", String.valueOf(price))
                        .replace("{current}", CURRENT)
                        .replace("{current-no-color}", CURRENT_NO_COLOR)));
                meta.setLore(lore);
                item.setItemMeta(meta);

                previousIsUnlocked = false;
            } else {
                // LOCKED item
                final ConfigurationSection ALL_LOCKED_CONFIG = this.API.getConfig(null).getConfigurationSection("all" +
                        ".locked");
                item = new ItemStack(Material.valueOf(ALL_LOCKED_CONFIG.getString("material")));

                ItemMeta meta = item.getItemMeta();
                List<String> lore;

                if (previousIsUnlocked) {
                    item.setType(Material.valueOf(ALL_LOCKED_CONFIG.getString("material-unlockable")));
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            ALL_LOCKED_CONFIG.getString("display-name-unlockable")
                                    .replace("{current}",
                                            ChatColor.translateAlternateColorCodes('&', ITEM_CONFIG.getString(
                                                    "display-name")))
                                    .replace("{current-no-color}",
                                            ChatColorRemover.removeChatColors(ITEM_CONFIG.getString("display-name")))));
                    lore = FISHING_CONFIG.getStringList("lore.locked");
                } else {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            ALL_LOCKED_CONFIG.getString("display-name")));
                    lore = ALL_LOCKED_CONFIG.getStringList("lore");
                }
                long amount = FISHING_DATA.getLong(previousKey + ".amount");
                long required = ITEM_CONFIG.getLong("amount");
                double price = ITEM_CONFIG.getDouble("price");
                final String PREVIOUS = FISHING_CONFIG.getString(previousKey + ".display-name");
                final String PREVIOUS_NO_COLOR = ChatColorRemover.removeChatColors(PREVIOUS);
                lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{required}", String.valueOf(required))
                        .replace("{price}", String.valueOf(price))
                        .replace("{previous}", PREVIOUS)
                        .replace("{previous-no-color}", PREVIOUS_NO_COLOR)));
                meta.setLore(lore);
                item.setItemMeta(meta);

                previousIsUnlocked = false;
            }


            trackedItems.add(item);
            previousKey = key;
        }

        // Add the tracked items
        List<Integer> slotsToSet = Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16, 17);

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
