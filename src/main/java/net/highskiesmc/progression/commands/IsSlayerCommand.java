package net.highskiesmc.progression.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.arcaniax.hdb.enums.CategoryEnum;
import net.highskiesmc.progression.HSProgression;
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
    private final HSProgression MAIN;

    public IsSlayerCommand(HSProgression main) {
        this.MAIN = main;
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
        if (!superiorPlayer.hasIsland()) {
            player.sendMessage(ChatColor.RED + "You need an island to run this command!");
            return;
        }

        Inventory inv = Bukkit.createInventory(player, 45, ChatColor.translateAlternateColorCodes('&', "&x&0&5&a&f&c" +
                "&6&lI&x&0&c&b&6&c&b&ls&x&1&4&b&d&d&0&ll&x&1&b&c&4&d&5&la&x&2&3&c&b&d&a&ln&x&2&a&d&2&d&f&ld " +
                "&x&3&2&d&a&e&4&lS&x&3&9&e&1&e&9&ll&x&4&1&e&8&e&e&la&x&4&8&e&f&f&3&ly&x&5&0&f&6&f&8&le&x&5&7&f&d&f&d" +
                "&lr"));

        final ConfigurationSection SLAYER_CONFIG = this.MAIN.getConfig().getConfigurationSection("slayer");

        List<ItemStack> trackedItems = new ArrayList<>();
        for (String key : SLAYER_CONFIG.getKeys(false)) {
            final ConfigurationSection ITEM_CONFIG = SLAYER_CONFIG.getConfigurationSection(key);
            //TODO
            // Grab different items based on what the island has unlocked so far
            // Set the last unlocked item to be enchanted in the GUI, or all unlocked ones
            ItemStack item = new HeadDatabaseAPI().getItemHead(ITEM_CONFIG.getString("head-id"));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ITEM_CONFIG.getString("display-name")));

            List<String> lore = ITEM_CONFIG.getStringList("lore.unlocked");
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);

            trackedItems.add(item);
        }

        // Add the tracked items
        List<Integer> slotsToSet = Arrays.asList(9, 18, 27, 28, 29, 20, 11, 12, 13, 22, 31, 32, 33, 24, 15, 16, 17);

        for (int i = 0; i < slotsToSet.size(); i++) {
            inv.setItem(slotsToSet.get(i), trackedItems.get(i));
        }

        // Fill in the rest of the GUI
        ItemStack placeholder =
                new ItemStack(Material.valueOf(this.MAIN.getConfig().getString("all.filler.material")));
        ItemMeta meta = placeholder.getItemMeta();
        meta.setDisplayName(" ");
        placeholder.setItemMeta(meta);

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, placeholder);
            }
        }

        player.openInventory(inv);
        //TODO
        // Check if player is apart of an island -- DONE
        // Only run the command if they are -- DONE
        // Grab their island and query the data.
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] strings) {
        return new ArrayList<>();
    }
}
