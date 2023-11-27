package net.highskiesmc.hsprogression.commands.superior;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import net.highskiesmc.hsprogression.inventories.IslandUpgradeGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IslandUpgradeCommand implements SuperiorCommand {
    @Override
    public List<String> getAliases() {
        return Arrays.asList("upgrade", "level", "levels", "upgrades");
    }

    @Override
    public String getPermission() {
        return "hsprogression.cmd.upgrade";
    }

    @Override
    public String getUsage(Locale locale) {
        return "upgrade";
    }

    @Override
    public String getDescription(Locale locale) {
        return "Opens the upgrade menu for your island";
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
    public void execute(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] strings) {
        // TODO: Make it so user has to be mod or admin to upgrade the island

        //TODO: Make islandLevel take the player's actual island level
        ((Player)commandSender).openInventory(new IslandUpgradeGUI(1).getInventory());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] strings) {
        return null;
    }
}
