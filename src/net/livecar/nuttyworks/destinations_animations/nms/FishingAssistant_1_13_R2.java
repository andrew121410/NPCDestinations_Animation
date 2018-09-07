package net.livecar.nuttyworks.destinations_animations.nms;

import java.util.Date;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import net.minecraft.server.v1_13_R2.EntityFishingHook;
import net.minecraft.server.v1_13_R2.WorldServer;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.Destinations_Animations;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings;

public class FishingAssistant_1_13_R2 implements FishingAssistant {
    private double hookGravity = 0.125D;

    public FishingAssistant_1_13_R2(Double gravity) {
        hookGravity = gravity;
    }

    @SuppressWarnings("deprecation")
    public void FishingCycle(NPC npc, Animations_Settings animationSettings, boolean stockFish) {
        Random rnd = new Random(new Date().getTime());

        if (animationSettings.fishHook != null) {
            if ((animationSettings.hookDestination.getY() - animationSettings.fishHook.getLocation().getY()) > 0.2D && ((animationSettings.castTime.getTime() + 2500) < new Date().getTime())) {
                // Got a fish.
                if (stockFish) {
                    // Need to add a random fish to the inventory of the NPC
                    Double perc = (Math.random() * 100) + 1;
                    ItemStack[] drop = new ItemStack[1];
                    if (perc < 60) {
                        // raw fish
                        Destinations_Animations.Instance.getDestinationsPlugin.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Fish Caught (Raw_Fish:0) Fish");
                        drop[0] = new ItemStack(Material.TROPICAL_FISH, 1, (short) 0);
                    } else if (perc < 75) {
                        // raw salmon
                        Destinations_Animations.Instance.getDestinationsPlugin.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Fish Caught (Raw_Fish:1) Salmon");
                        drop[0] = new ItemStack(Material.TROPICAL_FISH, 1, (short) 1);
                    } else if (perc < 78) {
                        // clownfish
                        Destinations_Animations.Instance.getDestinationsPlugin.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Fish Caught (Raw_Fish:1) Clown");
                        drop[0] = new ItemStack(Material.TROPICAL_FISH, 1, (short) 2);
                    } else {
                        // pufferfish
                        Destinations_Animations.Instance.getDestinationsPlugin.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Fish Caught (Raw_Fish:1) Puffer");
                        drop[0] = new ItemStack(Material.TROPICAL_FISH, 1, (short) 3);
                    }
                    Destinations_Animations.Instance.getProcessingClass.addToInventory(npc, drop);
                }
                animationSettings.fishHook.remove();
                animationSettings.fishHook = null;
                animationSettings.castTime = null;
                animationSettings.nextAnimationTime = new Date(System.currentTimeMillis() + 1000l);
                return;
            }
        }

        if (animationSettings.nextAnimationTime.getTime() > new Date().getTime())
            return;

        boolean pausePlayers = false;

        // Check the area near the NPC for players. Pause if so
        for (Player plrEntity : Bukkit.getOnlinePlayers()) {
            if ((plrEntity.getWorld() == npc.getEntity().getWorld()) && (plrEntity.getLocation().distance(npc.getEntity().getLocation()) < 8)) {
                pausePlayers = true;
            }
        }

        animationSettings.nextAnimationTime = new Date(System.currentTimeMillis() + 500l);

        if (animationSettings.fishHook != null) {
            // Pull the line in
            animationSettings.fishHook.remove();
            animationSettings.fishHook = null;
            animationSettings.castTime = null;
            animationSettings.nextAnimationTime = new Date(System.currentTimeMillis() + 1000l);
            Destinations_Animations.Instance.getDestinationsPlugin.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Hook Removed");
        } else if (!pausePlayers) {
            // Locate water
            int counter = 0;
            while (counter < 50) {
                Location source = new Location(npc.getEntity().getLocation().getWorld(), npc.getEntity().getLocation().getBlockX() + (16 - rnd.nextInt(32)), npc.getEntity().getLocation().getBlockY(), npc.getEntity().getLocation()
                        .getBlockZ() + (16 - rnd.nextInt(32)));
                for (byte y = -5; y <= 1; y++) {
                    Location checkLocation = source.clone().add(0.5, y, 0.5);
                    if (checkLocation.distanceSquared(npc.getEntity().getLocation()) > 8 && checkLocation.clone().add(0, -1, 0).getBlock().getType() == Material.WATER && checkLocation.clone().getBlock()
                            .getType() == Material.AIR) {
                        net.citizensnpcs.util.Util.faceLocation(npc.getEntity(), checkLocation);

                        try {
                            animationSettings.nextAnimationTime = new Date(System.currentTimeMillis() + 8000l + (1000l * rnd.nextInt(20)));
                            net.citizensnpcs.util.PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
                            animationSettings.hookDestination = checkLocation;
                            Destinations_Animations.Instance.getDestinationsPlugin.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Hook Cast(" + checkLocation + ")");
                            animationSettings.castTime = new Date();
                            animationSettings.fishHook = Destinations_Animations.Instance.nmsFishing.CastFishingLine(checkLocation, npc, 1);
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    }
                }
                counter++;
            }
        }
        animationSettings.nextAnimationTime = new Date(System.currentTimeMillis() + 1000l);
    }

    
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

    // Code borrowed from @SethBling :)
    public Vector calculateVelocity(Vector from, Vector to, int heightGain) {
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
