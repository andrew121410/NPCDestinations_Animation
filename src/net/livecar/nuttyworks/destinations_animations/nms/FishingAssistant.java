package net.livecar.nuttyworks.destinations_animations.nms;

import org.bukkit.Location;
import org.bukkit.entity.FishHook;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings;

public interface FishingAssistant {
    public FishHook CastFishingLine(Location destination, NPC npc, int gain);
    public void FishingCycle(NPC npc, Animations_Settings animationSettings, boolean stockFish);
}
