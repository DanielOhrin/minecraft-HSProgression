package net.highskiesmc.hsprogression.commands.tabcompleters;

import net.highskiesmc.hscore.highskies.HSTabCompleter;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class HSProgressionTabCompleter extends HSTabCompleter {
    private final HSProgressionApi api;
    private final Map<IslandProgressionType, List<String>> labels;
    public HSProgressionTabCompleter(HSProgressionApi api) {
        super();

        this.api = api;
        labels = getLabels();
    }
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("hsprogression.cmd.tab");
    }

    // /hsprogression giverecipe Crop IGN [Amount]
    // /hsp unlock <player> <skill> <level>
    @Override
    public List<String> getResults(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label,
                                   @NonNull String[] args) {
        List<String> result = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                result.add("reload");
                result.add("giverecipe");
                result.add("unlock");
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("giverecipe")) {
                    List<String> crops =
                            new ArrayList<>(HSProgression.getApi().getFarmingLevels().stream().map(x -> x.getCrop().name()).toList());
                    crops.remove(0);
                    result.addAll(crops.stream().filter(x -> x.toLowerCase().startsWith(args[1].toLowerCase())).toList());
                } else if (args[0].equalsIgnoreCase("unlock")) {
                    return matchOnlinePlayers(args[1], true);
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("giverecipe")) {
                    return matchOnlinePlayers(args[2], true);
                } else if (args[0].equalsIgnoreCase("unlock")) {
                    return enumValues(IslandProgressionType.ISLAND);
                }
            }
            case 4 -> {
                if (args[0].equalsIgnoreCase("giverecipe")) {
                    result.add("[amount]");
                } else if (args[0].equalsIgnoreCase("unlock")) {
                    IslandProgressionType levelType;

                    try {
                        levelType = IslandProgressionType.valueOf(args[2].toUpperCase());
                    } catch (EnumConstantNotPresentException ignore) {
                        return Collections.singletonList("Invalid skill");
                    }

                    return labels.get(levelType);
                }
            }
        }

        return result;
    }

    private Map<IslandProgressionType, List<String>> getLabels() {
        List<String> iLevels = api.getIslandLevels().stream().map(x -> String.valueOf(x.getLevel())).toList();
        List<String> sLevels = api.getSlayerLevels().stream().map(x -> x.getEntity().name()).toList();
        List<String> mLevels = api.getMiningLevels().stream().map(MiningLevel::getNodeId).toList();
        List<String> faLevels = api.getFarmingLevels().stream().map(x -> x.getCrop().name()).toList();
        List<String> fiLevels = api.getFishingLevels().stream().map(FishingLevel::getId).toList();

        return new HashMap<>(){{
            put(IslandProgressionType.ISLAND, iLevels);
            put(IslandProgressionType.SLAYER, sLevels);
            put(IslandProgressionType.MINING, mLevels);
            put(IslandProgressionType.FARMING, faLevels);
            put(IslandProgressionType.FISHING, fiLevels);
        }};
    }
}
