package net.livecar.nuttyworks.destinations_animations.storage;

import net.citizensnpcs.api.npc.NPC;

import java.util.List;
import java.util.UUID;

public class SleepPacket_Settings {
    public UUID playerID = null;
    public List<String> playedNPCS = null;
    
    public void addNPC(NPC npc) {
        if (playedNPCS != null && !playedNPCS.contains(String.valueOf(npc.getId())))
            playedNPCS.add(String.valueOf(npc.getId()));
        
    }
    
    public void removeNPC(NPC npc) {
        if (playedNPCS != null && playedNPCS.contains(String.valueOf(npc.getId())))
            playedNPCS.remove(String.valueOf(npc.getId()));
    }
    
    public boolean containsNPC(NPC npc) {
        if (playedNPCS != null)
            return playedNPCS.contains(String.valueOf(npc.getId()));
        else
            return false;
    }
    
}
