package net.highskiesmc.hsprogression.commands.superior;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.hscore.configuration.Config;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.inventories.IslandSlayerGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class IslandSlayerCommand implements SuperiorCommand {
    private final HSPlugin main;
    private final Config config;

    public IslandSlayerCommand(HSPlugin main) {
        super();
        this.main = main;
        this.config = main.getConfigs();
    }

    @Override
    public List<String> getAliases() {
        return List.of("slayer");
    }

    @Override
    public String getPermission() {
        return "hsprogression.cmd.slayer";
    }

    @Override
    public String getUsage(Locale locale) {
        return "slayer";
    }

    @Override
    public String getDescription(Locale locale) {
        return "Opens the slayer menu for your island";
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
        Player player = (Player) commandSender;
        SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(player);

        if (!sPlayer.hasIsland()) {
            player.sendMessage(TextUtils.translateColor(
                    config.get("common.no-island", String.class, "&c&lError | &7You don't have an island.")
            ));
            return;
        }

        Island island = sPlayer.getIsland();
        (player).openInventory(new IslandSlayerGUI((HSProgression) main, player, island).getInventory());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] strings) {
        return null;
    }
}
