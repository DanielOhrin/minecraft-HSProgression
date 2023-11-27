package net.highskiesmc.hsprogression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.commands.superior.IslandUpgradeCommand;
import net.highskiesmc.hsprogression.events.handlers.CommandPreProcessHandler;
import net.highskiesmc.hsprogression.events.handlers.IslandEventsHandler;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// TODO: REVAMP *resources* directory
public class HSProgression extends HSPlugin {
    private static HSProgressionApi api;

    @Override
    public void enable() {
        //<editor-fold desc="Island Levels">
        // Register SuperiorCommands
        SuperiorSkyblockAPI.registerCommand(new IslandUpgradeCommand());

        // Register Event Handlers
        register(new CommandPreProcessHandler());
        register(new IslandEventsHandler(this));
        //</editor-fold>
        //<editor-fold desc="API">
        try {
            api = new HSProgressionApi(this, Objects.requireNonNull(getConfig().getConfigurationSection("my-sql")));
        } catch (java.lang.Exception ex) {
            Exception.useStackTrace(getLogger()::severe, ex);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        //</editor-fold>

        return;
    }

    @Override
    public void disable() {
        api.dispose();
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
        return new HashSet<>() {{
            add("farming.yml");
            add("fishing.yml");
            add("slayer.yml");
            add("mining.yml");
        }};
    }

    public static HSProgressionApi getApi() {
        return api;
    }
}
