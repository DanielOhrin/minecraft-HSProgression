package net.highskiesmc.hsprogression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.api.Database;
import net.highskiesmc.hsprogression.commands.superior.IslandUpgradeCommand;
import net.highskiesmc.hsprogression.events.handlers.CommandPreProcessHandler;
import org.bukkit.Bukkit;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
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

        try {
            Database db = new Database(getConfig().getConfigurationSection("my-sql"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        return new HashSet<>() {{
            add("farming.yml");
            add("fishing.yml");
            add("slayer.yml");
            add("mining.yml");
        }};
    }
}
