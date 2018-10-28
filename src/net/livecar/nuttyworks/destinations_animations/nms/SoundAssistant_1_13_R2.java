package net.livecar.nuttyworks.destinations_animations.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Location;

public class SoundAssistant_1_13_R2 implements SoundAssistant {

    @Override
    public void PlaySound(Location location, Animations_Location setting) {
        for (Player plrEntity : Bukkit.getOnlinePlayers()) {
            if (plrEntity.getWorld() == location.getWorld()) {
                if (plrEntity.getLocation().distanceSquared(location) < 2116)
                {
                    SoundCategory sndCat = SoundCategory.valueOf(setting.soundCategory.toString());
                    plrEntity.playSound(location, setting.sound, sndCat, setting.volume, setting.pitch);
                }
            }
        }
    }
}
