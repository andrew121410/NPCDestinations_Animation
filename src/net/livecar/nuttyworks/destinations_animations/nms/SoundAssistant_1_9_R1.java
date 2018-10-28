package net.livecar.nuttyworks.destinations_animations.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Location;

public class SoundAssistant_1_9_R1 implements SoundAssistant {

    @Override
    public void PlaySound(Location location, Animations_Location setting) {
        for (Player plrEntity : Bukkit.getOnlinePlayers()) {
            if (plrEntity.getWorld() == location.getWorld()) {
                if (plrEntity.getLocation().distanceSquared(location) < 2116)
                {
                    plrEntity.playSound(location, setting.sound, setting.volume, setting.pitch);
                }
            }
        }
    }
}
