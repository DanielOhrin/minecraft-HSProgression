package net.highskiesmc.hsprogression.events.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreProcessHandler implements Listener {
    @EventHandler
    public void onIslandUpgradeCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().toLowerCase();

        // Bypasses SuperiorSkyblock's default upgrade command
        if (cmd.matches("^(?:is(?:lands?)?) upgrade")) {
            e.setMessage(cmd.replaceFirst("^(?:is|island|islands) upgrade", "is upgrades"));
        }
    }
}
