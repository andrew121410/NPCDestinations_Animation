package net.livecar.nuttyworks.destinations_animations.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_destinations.DestinationsPlugin;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.Entity;
import net.minecraft.server.v1_9_R2.EntityLiving;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PacketPlayOutAnimation;
import net.minecraft.server.v1_9_R2.PacketPlayOutBed;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;

public class BedAssistant_1_9_R2 implements BedAssistant {
    @SuppressWarnings({ "deprecation", "unused" })
    public void SleepNPC(NPC npc, Player player, float yaw) {
        final NPC thisNPC = npc;
        final Player plr = (Player) player;
        final NPCDestinationsTrait destTrait = thisNPC.getTrait(NPCDestinationsTrait.class);
        final EntityPlayer playerEntity = (EntityPlayer) getHandle(((Player) thisNPC.getEntity()));

        byte bedData = 12;
        double bedAdjustX = 0;
        double bedAdjustZ = 0;

        yaw = Math.abs(yaw);

        if (yaw < 45) {
            bedData = 10;
            bedAdjustX = 0.50;
            bedAdjustZ = 0.05;
        } else if (yaw < 135) {
            bedData = 13;
            bedAdjustX = 0.05;
            bedAdjustZ = 0.50;
        } else if (yaw < 225) {
            bedData = 8;
            bedAdjustZ = 0.95;
            bedAdjustX = 0.50;
        } else if (yaw < 315) {
            bedData = 11;
            bedAdjustX = 0.95;
            bedAdjustZ = 0.5;
        } else {
            bedData = 10;
            bedAdjustX = 0.50;
            bedAdjustZ = 0.05;
        }

        final byte bedByte = bedData;
        final Block srcBlock = destTrait.currentLocation.destination.getBlock();
        final Location nLoc = new Location(destTrait.currentLocation.destination.getWorld(), destTrait.currentLocation.destination.getBlockX() + bedAdjustX, destTrait.currentLocation.destination.getBlockY() + 0.2D, thisNPC.getEntity()
                .getLocation().getBlockZ() + bedAdjustZ);

        if (!destTrait.currentLocation.destination.getBlock().getType().equals(Material.BED_BLOCK)) {
            plr.sendBlockChange(new Location(thisNPC.getEntity().getLocation().getWorld(), destTrait.currentLocation.destination.getBlockX(), 0, destTrait.currentLocation.destination.getBlockZ()), Material.BED_BLOCK, bedByte);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutBed bedPacket;
                if (!destTrait.currentLocation.destination.getBlock().getType().equals(Material.BED_BLOCK)) {
                    bedPacket = new PacketPlayOutBed(playerEntity, new BlockPosition(destTrait.currentLocation.destination.getBlockX(), 0, destTrait.currentLocation.destination.getBlockZ()));
                } else {
                    bedPacket = new PacketPlayOutBed(playerEntity, new BlockPosition(destTrait.currentLocation.destination.getBlockX(), destTrait.currentLocation.destination.getBlockY(), destTrait.currentLocation.destination.getBlockZ()));
                }
                ((EntityPlayer) getHandle(plr)).playerConnection.sendPacket(bedPacket);
            }
        }.runTaskLater(DestinationsPlugin.Instance, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (destTrait.currentLocation.destination.getBlock().getType().equals(Material.BED_BLOCK)) {
                    thisNPC.teleport(nLoc.add(0.0, 0.5, 0.0), TeleportCause.PLUGIN);
                } else {
                    thisNPC.teleport(nLoc.add(0.0, 0.2, 0.0), TeleportCause.PLUGIN);
                }
            }
        }.runTaskLater(DestinationsPlugin.Instance, 25L);
    }

    public void unsleepNPC(NPC npc) {
        EntityPlayer playerEntity = (EntityPlayer) getHandle(((Player) npc.getEntity()));
        PacketPlayOutAnimation resetPacket = new PacketPlayOutAnimation(playerEntity, 2);

        for (Player plrEntity : Bukkit.getOnlinePlayers()) {
            if (plrEntity.getWorld() == npc.getEntity().getWorld()) {
                ((EntityPlayer) getHandle(plrEntity)).playerConnection.sendPacket(resetPacket);
            }
        }
    }

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
