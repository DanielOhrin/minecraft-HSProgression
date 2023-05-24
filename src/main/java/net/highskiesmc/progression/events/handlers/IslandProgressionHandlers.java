package net.highskiesmc.progression.events.handlers;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.events.events.IslandProgressedEvent;
import net.highskiesmc.progression.events.events.IslandUpgradedEvent;
import net.highskiesmc.progression.util.ChatColorRemover;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class IslandProgressionHandlers implements Listener {
    private final HSProgressionAPI API;

    public IslandProgressionHandlers(HSProgressionAPI api) {
        this.API = api;
    }

    @EventHandler
    public void onIslandProgress(IslandProgressedEvent e) {
        final ConfigurationSection EVENT_CONFIG = this.API.getConfig().getConfigurationSection("events" +
                ".island-progressed");
        final String DATA_TYPE = e.getIslandDataType().getValue();
        final String CURRENT = this.API.getConfig().getString(DATA_TYPE + '.' + e.getUnlockedKey() + ".display-name");
        final String CURRENT_NO_COLOR = ChatColorRemover.removeChatColors(CURRENT);

// CONSTRUCT MESSAGE
        final String MESSAGE = ChatColor.translateAlternateColorCodes('&', EVENT_CONFIG.getString("message")
                .replace("{data-type}", DATA_TYPE)
                .replace("{current}", CURRENT)
                .replace("{current-no-color}", CURRENT_NO_COLOR));

        // CONSTRUCT TITLE
        String TITLE_TITLE = EVENT_CONFIG.getString("title.title");
        if (TITLE_TITLE != null) {
            TITLE_TITLE = ChatColor.translateAlternateColorCodes('&', TITLE_TITLE
                    .replace("{data-type}", DATA_TYPE)
                    .replace("{current}", CURRENT)
                    .replace("{current-no-color}", CURRENT_NO_COLOR));
        }

        String TITLE_SUBTITLE = EVENT_CONFIG.getString("title.subtitle");
        if (TITLE_SUBTITLE != null) {
            TITLE_SUBTITLE = ChatColor.translateAlternateColorCodes('&', TITLE_SUBTITLE
                    .replace("{data-type}", DATA_TYPE)
                    .replace("{current}", CURRENT)
                    .replace("{current-no-color}", CURRENT_NO_COLOR));
        }
        final int TITLE_FADE_IN = EVENT_CONFIG.getInt("title.fade-in");
        final int TITLE_PERSIST = EVENT_CONFIG.getInt("title.persist");
        final int TITLE_FADE_OUT = EVENT_CONFIG.getInt("title.fade-out");

        List<SuperiorPlayer> islandMembers = e.getIsland().getIslandMembers(true);
        for (SuperiorPlayer member : islandMembers) {
            if (member.isOnline()) {
                Player player = Bukkit.getPlayer(member.getUniqueId());
                // PLAY SOUND
                player.playSound(player.getLocation(), Sound.valueOf(EVENT_CONFIG.getString("sound")), 1, 1);

                // SEND TITLE
                player.sendTitle(TITLE_TITLE, TITLE_SUBTITLE, TITLE_FADE_IN, TITLE_PERSIST, TITLE_FADE_OUT);

                // SEND MESSAGE
                player.sendMessage(MESSAGE);
            }
        }
    }

    @EventHandler
    void onIslandUpgraded(IslandUpgradedEvent e) {
        final ConfigurationSection EVENT_CONFIG = this.API.getConfig().getConfigurationSection("events" +
                ".island-upgraded");
        final String DATA_TYPE = e.getIslandDataType().getValue();
        final String CURRENT = this.API.getConfig().getString(DATA_TYPE + '.' + e.getUnlockedKey() + ".display-name");
        final String CURRENT_NO_COLOR = ChatColorRemover.removeChatColors(CURRENT);

        // CONSTRUCT MESSAGE
        final String MESSAGE = ChatColor.translateAlternateColorCodes('&', EVENT_CONFIG.getString("message")
                .replace("{data-type}", DATA_TYPE)
                .replace("{current}", CURRENT)
                .replace("{current-no-color}", CURRENT_NO_COLOR));

        // CONSTRUCT TITLE
        String TITLE_TITLE = EVENT_CONFIG.getString("title.title");
        if (TITLE_TITLE != null) {
            TITLE_TITLE = ChatColor.translateAlternateColorCodes('&', TITLE_TITLE
                    .replace("{data-type}", DATA_TYPE)
                    .replace("{current}", CURRENT)
                    .replace("{current-no-color}", CURRENT_NO_COLOR));
        }

        String TITLE_SUBTITLE = EVENT_CONFIG.getString("title.subtitle");
        if (TITLE_SUBTITLE != null) {
            TITLE_SUBTITLE = ChatColor.translateAlternateColorCodes('&', TITLE_SUBTITLE
                    .replace("{data-type}", DATA_TYPE)
                    .replace("{current}", CURRENT)
                    .replace("{current-no-color}", CURRENT_NO_COLOR));
        }
        final int TITLE_FADE_IN = EVENT_CONFIG.getInt("title.fade-in");
        final int TITLE_PERSIST = EVENT_CONFIG.getInt("title.persist");
        final int TITLE_FADE_OUT = EVENT_CONFIG.getInt("title.fade-out");

        List<SuperiorPlayer> islandMembers = e.getIsland().getIslandMembers(true);
        for (SuperiorPlayer member : islandMembers) {
            if (member.isOnline()) {
                Player player = Bukkit.getPlayer(member.getUniqueId());
                // PLAY SOUND
                player.playSound(player.getLocation(), Sound.valueOf(EVENT_CONFIG.getString("sound")), 1, 1);

                // SEND TITLE
                player.sendTitle(TITLE_TITLE, TITLE_SUBTITLE, TITLE_FADE_IN, TITLE_PERSIST, TITLE_FADE_OUT);

                // SEND MESSAGE
                player.sendMessage(MESSAGE);
            }
        }
    }
}
