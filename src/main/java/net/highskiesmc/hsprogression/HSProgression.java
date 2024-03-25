package net.highskiesmc.hsprogression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import net.highskiesmc.hscore.configuration.sources.FileConfigSource;
import net.highskiesmc.hscore.configuration.sources.XmlConfigSource;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.api.HSProgressionApi;
import net.highskiesmc.hsprogression.commands.commands.HSProgressionCommand;
import net.highskiesmc.hsprogression.commands.superior.*;
import net.highskiesmc.hsprogression.commands.tabcompleters.HSProgressionTabCompleter;
import net.highskiesmc.hsprogression.events.handlers.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;

public class HSProgression extends HSPlugin {
    private static HSProgressionApi api;
    private static Economy econ = null;
    private static RoseStackerAPI rsAPI = null;

    @Override
    public void enable() {
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!",
                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("RoseStacker")) {
            rsAPI = RoseStackerAPI.getInstance();
        }

        config.addSource(new FileConfigSource("config.yml", this));
        config.addSource(new FileConfigSource("messages.yml", this));
        config.addSource(new XmlConfigSource(this, "/config.xml"));
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
        SuperiorSkyblockAPI.registerCommand(new IslandSlayerCommand(this));
        SuperiorSkyblockAPI.registerCommand(new IslandFarmingCommand(this));
        SuperiorSkyblockAPI.registerCommand(new IslandMiningCommand(this));
        SuperiorSkyblockAPI.registerCommand(new IslandFishingCommand(this));

        // Register Plugin Commands
        getCommand("hsprogression").setExecutor(new HSProgressionCommand(this, api));
        getCommand("hsprogression").setTabCompleter(new HSProgressionTabCompleter(api));

        // Register Event Handlers
        register(new CommandPreProcessHandler());
        register(new IslandEventsHandler(this, api));
        register(new IslandLevelRestrictionsHandler(this, api));
        register(new IslandSlayerEventsHandler(this, api));
        register(new IslandFarmingEventsHandler(this, api));
        register(new IslandMiningEventsHandler(this, api));
        register(new IslandFishingEventsHandler(this, api));
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

    @Override
    protected boolean isAddingCustomRecipe() {
        return false;
    }

    public static HSProgressionApi getApi() {
        return api;
    }

    public static Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static RoseStackerAPI getRsAPI() {
        return rsAPI;
    }
}
