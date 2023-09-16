package net.highskiesmc.progression.commands.superior;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IslandUpgradeCommand implements SuperiorCommand {
    @Override
    public List<String> getAliases() {
        return Arrays.asList("level", "levels", "upgrade", "upgrades");
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getUsage(Locale locale) {
        return null;
    }

    @Override
    public String getDescription(Locale locale) {
        return null;
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public boolean displayCommand() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] strings) {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage("hi - HSProgression");
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] strings) {
        return null;
    }
}
