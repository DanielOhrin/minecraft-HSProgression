package net.highskiesmc.progression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.mattisadev.mcore.highskies.HSPlugin;
import net.highskiesmc.progression.commands.superior.IslandUpgradeCommand;
import net.highskiesmc.progression.events.handlers.CommandPreProcessHandler;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

// TODO: REVAMP *resources* directory
public class HSProgression extends HSPlugin {

    @Override
    public void enable() {
        // Register SuperiorCommands
        SuperiorSkyblockAPI.registerCommand(new IslandUpgradeCommand());

        // Register Event Handlers
        register(new CommandPreProcessHandler());
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    @Override
    protected boolean isUsingInventories() {
        return true;
    }

    @Nonnull
    @Override
    protected Set<String> getConfigFileNames() {
        return new  HashSet<>()
        {{
            add("farming.yml");
            add("fishing.yml");
            add("slayer.yml");
            add("mining.yml");
        }};
    }
}
