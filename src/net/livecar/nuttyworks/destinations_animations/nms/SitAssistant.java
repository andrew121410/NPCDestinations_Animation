package net.livecar.nuttyworks.destinations_animations.nms;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings;
import net.livecar.nuttyworks.destinations_animations.storage.Sitting_NPC;

public interface SitAssistant {
    public Sitting_NPC sitNPC(NPC npc, Animations_Settings animationSettings);

    public void unSitNPC(Sitting_NPC setting);
}
