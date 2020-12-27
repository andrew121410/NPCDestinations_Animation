package net.livecar.nuttyworks.destinations_animations.nms;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Location;
import net.livecar.nuttyworks.destinations_animations.plugin.FacingDirection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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
    
    //Sound Methods
    void PlaySound(Location location, Animations_Location setting);
    
    void AddToComposter(NPC npc, Location composter);
    
    ItemStack[] GetBlockInventory(Block block);
    
    void SetBlockInventory(Block block, ItemStack[] inventory);
    
    void SetPassenger(Entity vehicle, Entity passenger);
    
    FacingDirection getBlockDirection(Block block);
}
