package net.livecar.nuttyworks.destinations_animations.nms;

import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;

public interface BedAssistant {
    public void SleepNPC(NPC npc, Player player, float yaw);

    public void unsleepNPC(NPC npc);
}
