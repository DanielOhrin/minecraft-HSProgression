package net.highskiesmc.hsprogression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.highskiesmc.hscore.configuration.sources.FileConfigSource;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.commands.superior.IslandUpgradeCommand;
import net.highskiesmc.hsprogression.events.handlers.CommandPreProcessHandler;
import net.highskiesmc.hsprogression.events.handlers.IslandEventsHandler;
import net.highskiesmc.hsprogression.events.handlers.IslandLevelRestrictionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class HSProgression extends HSPlugin {
    private static HSProgressionApi api;
    @Override
    public void enable() {
        config.addSource(new FileConfigSource("config.yml", this));
        config.addSource(new FileConfigSource("messages.yml", this));
        config.reload();

        //<editor-fold desc="API">
        try {
            api = new HSProgressionApi(this, config.get("my-sql", ConfigurationSection.class, null));
        } catch (java.lang.Exception ex) {
            Exception.useStackTrace(getLogger()::severe, ex);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        //</editor-fold>

        //<editor-fold desc="Island Levels">
        // Register SuperiorCommands
        SuperiorSkyblockAPI.registerCommand(new IslandUpgradeCommand(this));

        // Register Event Handlers
        register(new CommandPreProcessHandler());
        register(new IslandEventsHandler(this, api));
        register(new IslandLevelRestrictionsHandler(this, api));
        //</editor-fold>

        return;
    }

    @Override
    public void disable() {
        if (api != null) {
            api.dispose();
        }
    }

    @Override
    public void reload() {

    }

    @Override
    protected boolean isUsingInventories() {
        return true;
    }

    public static HSProgressionApi getApi() {
        return api;
    }
}
