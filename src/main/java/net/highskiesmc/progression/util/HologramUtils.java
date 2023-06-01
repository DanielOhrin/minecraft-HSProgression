package net.highskiesmc.progression.util;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.common.value.qual.IntRange;

import java.util.Random;

public abstract class HologramUtils {
    public static void spawnAnimated(JavaPlugin plugin, Location location, String text,
                                     @IntRange(from = -1, to = 1) double minOffset,
                                     @IntRange(from = -1, to = 1) double maxOffset) {
        Random random = new Random();
        final double OFFSET_X = random.nextDouble(minOffset, maxOffset);
        final double OFFSET_Y = random.nextDouble(minOffset, maxOffset);
        final double OFFSET_Z = random.nextDouble(minOffset, maxOffset);

        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(
                location.add(OFFSET_X, OFFSET_Y - 0.5, OFFSET_Z),
                EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setGravity(false);

        stand.setCustomNameVisible(true);
        stand.setCustomName(text);


        // Animate the stand
        new BukkitRunnable() {
            private int ticksRan = 0;

            @Override
            public void run() {
                if (ticksRan == 50) {
                    stand.remove();
                    cancel();
                    return;
                }

                if (ticksRan < 15) {
                    stand.teleport(location.add(0, 0.066, 0));
                }

                ticksRan++;
            }
        }.runTaskTimer(plugin, 0, 1);

    }
}
