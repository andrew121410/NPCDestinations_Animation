package net.livecar.nuttyworks.destinations_animations.nms;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Location;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings;
import net.livecar.nuttyworks.destinations_animations.storage.Sitting_NPC;
import net.livecar.nuttyworks.npc_destinations.DestinationsPlugin;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class VersionBridge_1_13_R2 implements VersionBridge {
    //Bed Variables
    private final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private final BlockFace[] radial = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
    //Fishing Variables
    private double hookGravity = 0.125D;

    private static EntityLiving getHandle(LivingEntity entity) {
        return (EntityLiving) getHandle((org.bukkit.entity.Entity) entity);
    }

    private static Entity getHandle(org.bukkit.entity.Entity entity) {
        if (!(entity instanceof CraftEntity)) {
            return null;
        }
        return ((CraftEntity) entity).getHandle();
    }

    // Sleeping Code
    @Override
    public void SleepNPC(NPC npc, Player player, float yaw) {
        final NPC thisNPC = npc;
        final Player plr = (Player) player;
        final NPCDestinationsTrait destTrait = thisNPC.getTrait(NPCDestinationsTrait.class);
        final EntityPlayer playerEntity = (EntityPlayer) getHandle(((Player) thisNPC.getEntity()));

        double bedAdjustX = 0;
        double bedAdjustZ = 0;

        final Location nLoc = new Location(destTrait.currentLocation.destination.getWorld(), destTrait.currentLocation.destination.getBlockX() + bedAdjustX, destTrait.currentLocation.destination.getBlockY() + 0.2D, thisNPC.getEntity()
                .getLocation().getBlockZ() + bedAdjustZ);

        if (!(destTrait.currentLocation.destination.getBlock().getBlockData() instanceof Bed)) {
            Bed bedBlock = (Bed) Material.BLACK_BED.createBlockData();
            bedBlock.setFacing(this.yawToFace(yaw));
            plr.sendBlockChange(new Location(thisNPC.getEntity().getLocation().getWorld(), destTrait.currentLocation.destination.getBlockX(), 0, destTrait.currentLocation.destination.getBlockZ()), Material.BLACK_BED.createBlockData());
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
                    thisNPC.teleport(nLoc.add(0.0, 0.5, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                } else {
                    //thisNPC.teleport(nLoc.add(0.0, 0.2, 0.0), TeleportCause.PLUGIN);
                }
            }
        }.runTaskLater(DestinationsPlugin.Instance, 25L);
    }

    @Override
    public void unsleepNPC(NPC npc) {
        EntityPlayer playerEntity = (EntityPlayer) getHandle(((Player) npc.getEntity()));
        PacketPlayOutAnimation resetPacket = new PacketPlayOutAnimation(playerEntity, 2);

        for (Player plrEntity : Bukkit.getOnlinePlayers()) {
            if (plrEntity.getWorld() == npc.getEntity().getWorld()) {
                ((EntityPlayer) getHandle(plrEntity)).playerConnection.sendPacket(resetPacket);
            }
        }
    }

    //Chest animation code
    @Override
    public void changeChestState(Location blockLocation, Boolean open) {
        int openClose = (open) ? (byte) 1 : 0; // The byte of data used for the
        // note and animation packet (1
        // if true, 0 if false)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (open)
                player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_CHEST_OPEN"), 1F, 1F);
            if (!open)
                player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_CHEST_CLOSE"), 1F, 1F);


            BlockPosition position = new BlockPosition(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ());
            PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, Blocks.CHEST, 1, openClose);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(blockActionPacket);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack[] getFishDrops() {
        // Need to add a random fish to the inventory of the NPC
        Double perc = (Math.random() * 100) + 1;
        ItemStack[] drop = new ItemStack[1];
        if (perc < 60) {
            // raw fish
            DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Fish Caught (Raw_Fish:0) Fish");
            drop[0] = new ItemStack(Material.TROPICAL_FISH, 1, (short) 0);
        } else if (perc < 75) {
            // raw salmon
            DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Fish Caught (Raw_Fish:1) Salmon");
            drop[0] = new ItemStack(Material.TROPICAL_FISH, 1, (short) 1);
        } else if (perc < 78) {
            // clownfish
            DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Fish Caught (Raw_Fish:1) Clown");
            drop[0] = new ItemStack(Material.TROPICAL_FISH, 1, (short) 2);
        } else {
            // pufferfish
            DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Fish Caught (Raw_Fish:1) Puffer");
            drop[0] = new ItemStack(Material.TROPICAL_FISH, 1, (short) 3);
        }
        return drop;
    }

    @Override
    public Material getWaterBlock() {
        return Material.WATER;
    }

    @Override
    public FishHook CastFishingLine(Location destination, NPC npc, int gain) {
        Location from = npc.getEntity().getLocation().clone();
        from = from.add(0, .33, 0);

        WorldServer nmsWorld = ((CraftWorld) destination.getWorld()).getHandle();
        EntityFishingHook hook = new EntityFishingHook(nmsWorld, ((CraftPlayer) npc.getEntity()).getHandle());
        nmsWorld.addEntity(hook);
        FishHook thisHook = (FishHook) hook.getBukkitEntity();
        thisHook.setShooter((ProjectileSource) npc.getEntity());
        thisHook.setVelocity(calculateVelocity(from.toVector(), destination.toVector(), gain));
        return thisHook;
    }

    @Override
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
        sitStand.addPassenger(npc.getEntity());

        return sitNPC;
    }

    @Override
    public void unSitNPC(Sitting_NPC setting) {
        setting.npc.getEntity().eject();
        setting.npc.teleport(setting.npc.getEntity().getLocation().clone().add(0, 0.5, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
        setting.attachedArmorStand.remove();
    }

    @Override
    public void PlaySound(Location location, Animations_Location setting) {
        for (Player plrEntity : Bukkit.getOnlinePlayers()) {
            if (plrEntity.getWorld() == location.getWorld()) {
                if (plrEntity.getLocation().distanceSquared(location) < 2116) {
                    org.bukkit.SoundCategory sndCat = SoundCategory.valueOf(setting.soundCategory.toString());
                    plrEntity.playSound(location, setting.sound, sndCat, setting.volume, setting.pitch);
                }
            }
        }
    }

    private BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }

    private BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }

    // Code borrowed from @SethBling :)
    private Vector calculateVelocity(Vector from, Vector to, int heightGain) {
        // Block locations
        int endGain = to.getBlockY() - from.getBlockY();
        double horizDist = Math.sqrt(distanceSquared(from, to));

        // Height gain
        int gain = heightGain;

        double maxGain = gain > (endGain + gain) ? gain : (endGain + gain);

        // Solve quadratic equation for velocity
        double a = -horizDist * horizDist / (4 * maxGain);
        double b = horizDist;
        double c = -endGain;

        double slope = -b / (2 * a) - Math.sqrt(b * b - 4 * a * c) / (2 * a);

        // Vertical velocity
        double vy = Math.sqrt(maxGain * hookGravity);

        // Horizontal velocity
        double vh = vy / slope;

        // Calculate horizontal direction
        double dx = (to.getBlockX() - from.getBlockX()) + 0.5;
        double dz = (to.getBlockZ() - from.getBlockZ()) + 0.5;
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;

        // Horizontal velocity components
        double vx = vh * dirx;
        double vz = vh * dirz;

        return new Vector(vx, vy, vz);
    }

    private double distanceSquared(Vector from, Vector to) {
        double dx = to.getBlockX() - from.getBlockX();
        double dz = to.getBlockZ() - from.getBlockZ();

        return dx * dx + dz * dz;
    }

}
