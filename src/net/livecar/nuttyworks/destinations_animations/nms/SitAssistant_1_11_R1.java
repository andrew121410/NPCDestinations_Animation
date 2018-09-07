package net.livecar.nuttyworks.destinations_animations.nms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings;
import net.livecar.nuttyworks.destinations_animations.storage.Sitting_NPC;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityLiving;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;

public class SitAssistant_1_11_R1 implements SitAssistant {

    public Sitting_NPC sitNPC(NPC npc, Animations_Settings animationSettings) {

        int xAxis = 0;
        int zAxis = 0;

        double rotation = animationSettings.destinationsTrait.currentLocation.destination.getYaw();
        Location sitLocation = animationSettings.destinationsTrait.currentLocation.destination.clone();
        
        if (rotation < 30.0) {
            xAxis = 0;
            zAxis = 1;
        } else if (rotation < 60) {
            xAxis = -1;
            zAxis = 1;
        } else if (rotation < 120) {
            xAxis = -1;
            zAxis = 0;
        } else if (rotation < 150) {
            xAxis = -1;
            zAxis = -1;
        } else if (rotation < 210) {
            xAxis = 0;
            zAxis = -1;
        } else if (rotation < 240) {
            xAxis = 1;
            zAxis = -1;
        } else if (rotation < 300) {
            xAxis = 1;
            zAxis = 0;
        } else if (rotation < 330) {
            xAxis = 1;
            zAxis = 1;
        } else {
            xAxis = 0;
            zAxis = 1;
        }
        
        if (sitLocation.getBlock().getType() == Material.AIR)
            sitLocation.add(0, -1, 0);
        
        if (sitLocation.getBlock().getType().toString().toLowerCase().contains("stairs"))
            sitLocation.add(0, -0.5, 0);
        
        final Location faceLocation = animationSettings.destinationsTrait.currentLocation.destination.clone().add(xAxis, 0, zAxis);
        net.citizensnpcs.util.Util.faceLocation(npc.getEntity(), faceLocation);
        
        ArmorStand sitStand = npc.getEntity().getWorld().spawn(sitLocation, ArmorStand.class);

        Sitting_NPC sitNPC = new Sitting_NPC();
        sitNPC.npc = npc;
        sitNPC.attachedArmorStand = (org.bukkit.entity.Entity) sitStand;

        sitStand.setInvulnerable(true);
        sitStand.setSilent(true);
        sitStand.setGravity(false);
        sitStand.setAI(false);
        sitStand.setArms(false);
        sitStand.setBasePlate(false);
        sitStand.setSmall(true);
        sitStand.setMarker(false);
        sitStand.setVisible(false);
        ((CraftEntity) sitStand).getHandle().setInvisible(true);
        npc.getEntity().setPassenger(sitStand);

        return sitNPC;
    }

    public void unSitNPC(Sitting_NPC setting) {
        setting.attachedArmorStand.remove();
    }

    @SuppressWarnings("unused")
    private static EntityLiving getHandle(LivingEntity entity) {
        return (EntityLiving) getHandle((org.bukkit.entity.Entity) entity);
    }

    public static Entity getHandle(org.bukkit.entity.Entity entity) {
        if (!(entity instanceof CraftEntity)) {
            return null;
        }
        return ((CraftEntity) entity).getHandle();
    }

}
