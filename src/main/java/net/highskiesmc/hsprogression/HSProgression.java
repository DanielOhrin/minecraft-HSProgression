package net.highskiesmc.hsprogression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hsprogression.commands.superior.IslandUpgradeCommand;
import net.highskiesmc.hsprogression.events.handlers.CommandPreProcessHandler;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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


        try (InputStream stream = HSProgression.class.getResourceAsStream("/config.xml")) {
            StringBuilder sb = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }

            Bukkit.getLogger().info(sb.toString());
        }
        catch (Exception ex){
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
        return new  HashSet<>()
        {{
            add("farming.yml");
            add("fishing.yml");
            add("slayer.yml");
            add("mining.yml");
        }};
    }
}
