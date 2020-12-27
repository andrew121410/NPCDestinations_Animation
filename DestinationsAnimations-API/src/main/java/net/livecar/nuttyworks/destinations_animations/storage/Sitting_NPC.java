package net.livecar.nuttyworks.destinations_animations.storage;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_destinations.DestinationsPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Sitting_NPC {
    public NPC npc = null;
    public Entity attachedArmorStand = null;
    public Location originalLocation = null;
    public Material originalBlock = null;
    private BukkitTask updateTask;
    
    public void StartSitting()
    {
        if (updateTask == null)
        {
            originalBlock = originalLocation.getBlock().getType();
            final Location standLocation = attachedArmorStand.getLocation();
            updateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!MonitorTask(standLocation))
                        this.cancel();
                }
            }.runTaskTimer(DestinationsPlugin.Instance, 0L,1L);
        }
    }
    
    public void StopSitting()
    {
        if (updateTask != null) {
            updateTask.cancel();
            npc.getEntity().eject();
            npc.teleport(originalLocation.add(0.0D, 0.5D, 0.0D), PlayerTeleportEvent.TeleportCause.PLUGIN);
            attachedArmorStand.remove();
        }
    }
    
    private boolean MonitorTask(Location standLocation)
    {
        if ((attachedArmorStand.getLocation().distanceSquared(standLocation) > 1) ||
            !npc.isSpawned() ||
            originalBlock != originalLocation.getBlock().getType())
        {
            //Unsit the NPC
            if (npc != null) {
                npc.getEntity().eject();
            }
            if (attachedArmorStand != null)
                attachedArmorStand.remove();
            return false;
        }
        return true;
    }
}
