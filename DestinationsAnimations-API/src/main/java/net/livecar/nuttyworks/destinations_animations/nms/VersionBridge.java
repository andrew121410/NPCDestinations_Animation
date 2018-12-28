package net.livecar.nuttyworks.destinations_animations.nms;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Location;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings;
import net.livecar.nuttyworks.destinations_animations.storage.Sitting_NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface VersionBridge {

    //Sleeping Methods
    void SleepNPC(NPC npc, Player player, float yaw);

    void unsleepNPC(NPC npc);

    //Chest Methods
    void changeChestState(Location blockLocation, Boolean open);

    //Fishing Methods
    ItemStack[] getFishDrops();

    Material getWaterBlock();

    FishHook CastFishingLine(Location destination, NPC npc, int gain);

    //Sitting Methods
    Sitting_NPC sitNPC(NPC npc, Animations_Settings animationSettings);

    void unSitNPC(Sitting_NPC setting);

    //Sound Methods
    void PlaySound(Location location, Animations_Location setting);

}
