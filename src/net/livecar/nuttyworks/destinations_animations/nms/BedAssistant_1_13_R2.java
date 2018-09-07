package net.livecar.nuttyworks.destinations_animations.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.npc_destinations.DestinationsPlugin;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutAnimation;
import net.minecraft.server.v1_13_R2.PacketPlayOutBed;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;

public class BedAssistant_1_13_R2 implements BedAssistant {

    
    private final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
       
    public void SleepNPC(NPC npc, Player player, float yaw) {
        final NPC thisNPC = npc;
        final Player plr = (Player) player;
        final NPCDestinationsTrait destTrait = thisNPC.getTrait(NPCDestinationsTrait.class);
        final EntityPlayer playerEntity = (EntityPlayer) getHandle(((Player) thisNPC.getEntity()));

        double bedAdjustX = 0;
        double bedAdjustZ = 0;

        final Location nLoc = new Location(destTrait.currentLocation.destination.getWorld(), destTrait.currentLocation.destination.getBlockX() + bedAdjustX, destTrait.currentLocation.destination.getBlockY() + 0.2D, thisNPC.getEntity()
                .getLocation().getBlockZ() + bedAdjustZ);

        if (!(destTrait.currentLocation.destination.getBlock().getBlockData() instanceof Bed))
        {
            Bed bedBlock = (Bed)Material.BLACK_BED.createBlockData();
            bedBlock.setFacing(this.yawToFace(yaw));
            plr.sendBlockChange(new Location(thisNPC.getEntity().getLocation().getWorld(), destTrait.currentLocation.destination.getBlockX(), 0, destTrait.currentLocation.destination.getBlockZ()),Material.BLACK_BED.createBlockData());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutBed bedPacket;
                if (!(destTrait.currentLocation.destination.getBlock().getBlockData() instanceof Bed)) {
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
                if (!(destTrait.currentLocation.destination.getBlock().getBlockData() instanceof Bed)) {
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
    

    /**
    * Gets the horizontal Block Face from a given yaw angle<br>
    * This includes the NORTH_WEST faces
    *
    * @param yaw angle
    * @return The Block Face of the angle
    */
    private BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }
    /**
    * Gets the horizontal Block Face from a given yaw angle
    *
    * @param yaw angle
    * @param useSubCardinalDirections setting, True to allow NORTH_WEST to be returned
    * @return The Block Face of the angle
    */
    private BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }
    
}
