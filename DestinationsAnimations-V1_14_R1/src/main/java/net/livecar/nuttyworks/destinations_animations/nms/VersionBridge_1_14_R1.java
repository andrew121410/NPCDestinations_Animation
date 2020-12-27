package net.livecar.nuttyworks.destinations_animations.nms;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Location;
import net.livecar.nuttyworks.destinations_animations.plugin.FacingDirection;
import net.livecar.nuttyworks.npc_destinations.DestinationsPlugin;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class VersionBridge_1_14_R1 implements VersionBridge {
    //Fishing Variables
    private double hookGravity = 0.125D;
    
    // Sleeping Code
    @Override
    public void SleepNPC(NPC npc, Player player, float yaw) {
        final NPCDestinationsTrait destTrait = npc.getTrait(NPCDestinationsTrait.class);
        final BlockPosition blockPosition = new BlockPosition(destTrait.currentLocation.destination.getX(),
                destTrait.currentLocation.destination.getY(),
                destTrait.currentLocation.destination.getZ());
        
        if (npc.getEntity() instanceof EntityPlayer) {
            final EntityPlayer playerEntity = (EntityPlayer) getHandle(((Player) npc.getEntity()));
            playerEntity.sleep(blockPosition, true);
        }
        
    }
    
    private static EntityLiving getHandle(LivingEntity entity) {
        return (EntityLiving) getHandle((org.bukkit.entity.Entity) entity);
    }
    
    private static Entity getHandle(org.bukkit.entity.Entity entity) {
        if (!(entity instanceof CraftEntity)) {
            return null;
        }
        return ((CraftEntity) entity).getHandle();
    }
    
    @Override
    public void unsleepNPC(NPC npc) {
        if (npc.getEntity() instanceof EntityPlayer) {
            EntityPlayer playerEntity = (EntityPlayer) getHandle(((Player) npc.getEntity()));
            playerEntity.wakeup(false, false, false);
        }
    }
    
    //Chest animation code
    @Override
    public void changeChestState(Location blockLocation, Boolean open) {
        int openClose = (open) ? (byte) 1 : 0; // The byte of data used for the
        // note and animation packet (1
        // if true, 0 if false)
        
        BlockPosition position = new BlockPosition(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ());
        PacketPlayOutBlockAction blockActionPacket;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            
            switch (blockLocation.getBlock().getType()) {
                case CHEST:
                case TRAPPED_CHEST:
                    if (open)
                        player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_CHEST_OPEN"), 1F, 1F);
                    if (!open)
                        player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_CHEST_CLOSE"), 1F, 1F);
                    
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockAction(position, Blocks.CHEST, 1, openClose));
                    break;
                case ENDER_CHEST:
                    if (open)
                        player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_ENDER_CHEST_OPEN"), 1F, 1F);
                    if (!open)
                        player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_ENDER_CHEST_CLOSE"), 1F, 1F);
                    
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockAction(position, Blocks.ENDER_CHEST, 1, openClose));
                    break;
                case SHULKER_BOX:
                case BLACK_SHULKER_BOX:
                case CYAN_SHULKER_BOX:
                case BLUE_SHULKER_BOX:
                case BROWN_SHULKER_BOX:
                case GRAY_SHULKER_BOX:
                case GREEN_SHULKER_BOX:
                case LIGHT_BLUE_SHULKER_BOX:
                case LIGHT_GRAY_SHULKER_BOX:
                case LIME_SHULKER_BOX:
                case MAGENTA_SHULKER_BOX:
                case ORANGE_SHULKER_BOX:
                case PINK_SHULKER_BOX:
                case PURPLE_SHULKER_BOX:
                case RED_SHULKER_BOX:
                case WHITE_SHULKER_BOX:
                case YELLOW_SHULKER_BOX:
                    if (open)
                        player.getWorld().playSound(blockLocation, Sound.valueOf("ENTITY_SHULKER_OPEN"), 1F, 1F);
                    if (!open)
                        player.getWorld().playSound(blockLocation, Sound.valueOf("ENTITY_SHULKER_CLOSE"), 1F, 1F);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockAction(position, Blocks.WHITE_SHULKER_BOX, 1, openClose));
                    break;
                case BARREL:
                    if (open)
                        player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_BARREL_OPEN"), 1F, 1F);
                    if (!open)
                        player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_BARREL_CLOSE"), 1F, 1F);
                    break;
            }
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
        EntityFishingHook hook = new EntityFishingHook(((CraftPlayer) npc.getEntity()).getHandle(), nmsWorld, 1, 1);
        nmsWorld.addEntity(hook);
        FishHook thisHook = (FishHook) hook.getBukkitEntity();
        thisHook.setShooter((ProjectileSource) npc.getEntity());
        thisHook.setVelocity(calculateVelocity(from.toVector(), destination.toVector(), gain));
        return thisHook;
    }
    
    @Override
    public void PlaySound(Location location, Animations_Location setting) {
        for (Player plrEntity : Bukkit.getOnlinePlayers()) {
            if (plrEntity.getWorld() == location.getWorld()) {
                if (plrEntity.getLocation().distanceSquared(location) < 2116) {
                    org.bukkit.SoundCategory sndCat = SoundCategory.PLAYERS;
                    try {
                        SoundCategory.valueOf(setting.soundCategory.toString());
                    } catch (Exception err) {
                    }
                    
                    plrEntity.playSound(location, setting.sound, sndCat, setting.volume, setting.pitch);
                }
            }
        }
    }
    
    @Override
    public void AddToComposter(NPC npc, Location composter) {
    
    }
    
    @Override
    public ItemStack[] GetBlockInventory(org.bukkit.block.Block block) {
        switch (block.getType()) {
            case CHEST:
            case TRAPPED_CHEST:
            case ENDER_CHEST:
                return ((Chest) block.getState()).getBlockInventory().getContents();
            case SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
                return ((ShulkerBox) block.getState()).getInventory().getContents();
            case BARREL:
                return ((Barrel) block.getState()).getInventory().getContents();
            default:
                return null;
        }
    }
    
    @Override
    public void SetBlockInventory(Block block, ItemStack[] inventory) {
        switch (block.getType()) {
            case CHEST:
            case TRAPPED_CHEST:
            case ENDER_CHEST:
                ((Chest) block.getState()).getBlockInventory().setContents(inventory);
                break;
            case SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
                ((ShulkerBox) block.getState()).getInventory().setContents(inventory);
                break;
            case BARREL:
                ((Barrel) block.getState()).getInventory().setContents(inventory);
                break;
        }
    }
    
    @Override
    public void SetPassenger(org.bukkit.entity.Entity vehicle, org.bukkit.entity.Entity passenger) {
        vehicle.addPassenger(passenger);
    }
    
    @Override
    public FacingDirection getBlockDirection(Block block) {
        if (block.getState().getData() instanceof Directional) {
            switch (((Directional) block.getState().getData()).getFacing())
            {
                case NORTH:
                    return FacingDirection.SOUTH;
                case EAST:
                    return FacingDirection.WEST;
                case SOUTH:
                    return FacingDirection.NORTH;
                case WEST:
                    return FacingDirection.EAST;
                case NORTH_EAST:
                    return FacingDirection.SOUTH_WEST;
                case NORTH_WEST:
                    return FacingDirection.SOUTH_EAST;
                case SOUTH_EAST:
                    return FacingDirection.NORTH_WEST;
                case SOUTH_WEST:
                    return FacingDirection.NORTH_EAST;
            }
        }
        return null;
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
