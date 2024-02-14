package net.highskiesmc.hsprogression.commands.tabcompleters;

import net.highskiesmc.hscore.highskies.HSTabCompleter;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class HSProgressionTabCompleter extends HSTabCompleter {
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("hsprogression.cmd.tab");
    }

    // /hsprogression giverecipe Crop IGN [Amount]
    @Override
    public List<String> getResults(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label,
                                   @NonNull String[] args) {
        List<String> result = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                result.add("reload");
                result.add("giverecipe");
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("giverecipe")) {
                    List<String> crops =
                            new ArrayList<>(HSProgression.getApi().getFarmingLevels().stream().map(x -> x.getCrop().name()).toList());
                    crops.remove(0);
                    result.addAll(crops.stream().filter(x -> x.toLowerCase().startsWith(args[1].toLowerCase())).toList());
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("giverecipe")) {
                    return matchOnlinePlayers(args[2], true);
                }
            }
            case 4 -> {
                if (args[0].equalsIgnoreCase("giverecipe")) {
                    result.add("[amount]");
                }
            }
        }

        return result;
    }
}
