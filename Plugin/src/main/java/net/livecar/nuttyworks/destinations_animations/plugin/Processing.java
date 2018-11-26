package net.livecar.nuttyworks.destinations_animations.plugin;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.livecar.nuttyworks.destinations_animations.Destinations_Animations;
import net.livecar.nuttyworks.destinations_animations.storage.Sitting_NPC;
import net.livecar.nuttyworks.destinations_animations.storage.SleepPacket_Settings;
import net.livecar.nuttyworks.npc_destinations.DestinationsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class Processing {
    private HashMap<UUID, SleepPacket_Settings> sleepPlayed;
    private HashMap<UUID, Sitting_NPC>          sittingNPC;
    private Destinations_Animations             pluginReference = null;

    public Processing(Destinations_Animations instanceRef) {
        pluginReference = instanceRef;
        sleepPlayed = new HashMap<UUID, SleepPacket_Settings>();
        sittingNPC = new HashMap<UUID, Sitting_NPC>();
    }

    public void onPluginTick() {
        for (Entry<Integer, UUID> entry : pluginReference.monitoredNPC.entrySet()) {
            NPC npc = pluginReference.getCitizensPlugin.getNPCRegistry().getById(entry.getKey());
            if (npc == null || !npc.isSpawned())
                continue;

            Animations_Settings animationSettings = pluginReference.npcSettings.get(npc.getId());
            if (!animationSettings.destinationsTrait.currentLocation.LocationIdent.equals(entry.getValue()))
                continue;

            if (animationSettings.destinationsTrait.currentLocation.destination.distanceSquared(npc.getEntity().getLocation()) > 4) {
                // Need to get the NPC back to it's location
                if (animationSettings.lastAction == null || animationSettings.lastAction.getTime() + 10000 < new Date().getTime() && !npc.getNavigator().isNavigating()) {
                    animationSettings.lastAction = new Date();
                    DestinationsPlugin.Instance.getPathClass.addToQueue(npc, animationSettings.destinationsTrait, npc.getEntity().getLocation().add(0.0D, -1.0D, 0.0D), animationSettings.destinationsTrait.currentLocation.destination, 120,
                            new ArrayList<Material>(), 0, true, true, true, "DestinationsAnimations");
                }
                continue;
            }

            switch (animationSettings.locations.get(entry.getValue()).action) {
            case FISH:
                FishingCycle(npc, animationSettings, false);
                continue;
            case FISH_ADD:
                FishingCycle(npc, animationSettings, true);
                continue;
            case NONE:
                continue;
            case SLEEP:
                this.SleepCycle(npc, animationSettings);
                continue;
            case SIT:
                sitNPC(npc, animationSettings);
                continue;
            case SWING:
                swingNPC(npc, animationSettings,animationSettings.locations.get(entry.getValue()));
                continue;
            default:
                continue;
            }
        }
    }

    public void swingNPC(NPC npc, Animations_Settings animationSettings, Animations_Location curLoc) {

        if ((curLoc.lastAction.getTime() + curLoc.interval < new Date().getTime()))
        {
            curLoc.lastAction = new Date();
            net.citizensnpcs.util.Util.assumePose(npc.getEntity(),  animationSettings.destinationsTrait.currentLocation.destination.clone().getYaw(), animationSettings.destinationsTrait.currentLocation.destination.clone().getPitch());
            net.citizensnpcs.util.PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
            if (curLoc.sound != null)
            {
                pluginReference.getNMSBridge.PlaySound(npc.getEntity().getLocation(), curLoc);
            }
        }
    }

    public void sitNPC(NPC npc, Animations_Settings animationSettings) {
        if (sittingNPC.containsKey(npc.getEntity().getUniqueId()))
            return;

        Bukkit.getServer().getLogger().log(Level.INFO, "AnimSit: " + npc.getId());

        sittingNPC.put(npc.getEntity().getUniqueId(), pluginReference.getNMSBridge.sitNPC(npc, animationSettings));
    }

    public void SleepCycle(NPC npc, Animations_Settings animationSettings) {
        if (!npc.isSpawned())
            return;

        if (animationSettings.destinationsTrait.getPendingDestinations() != null && animationSettings.destinationsTrait.getPendingDestinations().size() > 0)
            return;

        if (animationSettings.destinationsTrait.currentLocation == null)
            return;

        if (animationSettings.destinationsTrait.currentLocation.destination.distance(npc.getEntity().getLocation()) > 2)
            return;

        for (Player plrEntity : Bukkit.getOnlinePlayers()) {
            Location plrLocation = plrEntity.getLocation().clone();
            plrLocation.setY(0.0);
            Location destLocation = animationSettings.destinationsTrait.currentLocation.destination.clone();
            destLocation.setY(0.0);

            if (!sleepPlayed.containsKey(plrEntity.getUniqueId())) {
                SleepPacket_Settings plrPacket = new SleepPacket_Settings();
                plrPacket.playerID = plrEntity.getUniqueId();
                plrPacket.playedNPCS = new ArrayList<String>();
                sleepPlayed.put(plrEntity.getUniqueId(), plrPacket);
            }

            if ((plrEntity.getWorld() == npc.getEntity().getWorld()) && (plrLocation.distanceSquared(destLocation) < pluginReference.entityRadius) && !sleepPlayed.get(plrEntity.getUniqueId()).containsNPC(npc)) {
                animationSettings.isSleeping = true;
                sleepPlayed.get(plrEntity.getUniqueId()).addNPC(npc);
                pluginReference.getNMSBridge.SleepNPC(npc, plrEntity, animationSettings.destinationsTrait.currentLocation.destination.getYaw());
            } else if (plrEntity.getWorld().equals(npc.getEntity().getWorld()) && (plrLocation.distanceSquared(destLocation) >= pluginReference.entityRadius)) {
                // Remove the player from the list of packets being sent, so it
                // can resend if they come back
                if (sleepPlayed.containsKey(plrEntity.getUniqueId())) {
                    sleepPlayed.get(plrEntity.getUniqueId()).removeNPC(npc);
                }
            }
        }
        animationSettings.nextAnimationTime = new Date(System.currentTimeMillis() + 250l);
    }

    public void undoAnimations(NPC npc) {
        Animations_Settings animationSettings = pluginReference.npcSettings.get(npc.getId());

        if (animationSettings == null)
            return;

        pluginReference.getDestinationsPlugin.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.undoAnimations()");

        if (animationSettings.isSleeping) {
            for (Player plrEntity : Bukkit.getOnlinePlayers()) {
                pluginReference.getNMSBridge.unsleepNPC(npc);
                if (this.sleepPlayed.containsKey(plrEntity.getUniqueId())) {
                    this.sleepPlayed.get(plrEntity.getUniqueId()).removeNPC(npc);
                }
            }
            animationSettings.isSleeping = false;
        }

        if (sittingNPC.containsKey(npc.getEntity().getUniqueId())) {
            pluginReference.getNMSBridge.unSitNPC(sittingNPC.get(npc.getEntity().getUniqueId()));
            this.sittingNPC.remove(npc.getEntity().getUniqueId());
        }
    }

    public void openChest(NPC npc, Animations_Settings animationSettings, boolean stockChest) {
        int xAxis = 0;
        int zAxis = 0;

        double rotation = animationSettings.destinationsTrait.currentLocation.destination.getYaw();

        // North: -Z
        // East: +X
        // South: +Z
        // West: -X

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

        // Check if we have a chest at the location
        for (byte y = -1; y <= 1; y++) {
            final Location chestLocation = animationSettings.destinationsTrait.currentLocation.destination.clone().add(xAxis, y, zAxis);

            if (chestLocation.getBlock().getType() == Material.TRAPPED_CHEST || chestLocation.getBlock().getType() == Material.CHEST || chestLocation.getBlock().getType() == Material.ENDER_CHEST) {

                net.citizensnpcs.util.Util.faceLocation(npc.getEntity(), chestLocation);
                net.citizensnpcs.util.PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());

                // Found out chest, lets open it for a few moments
                Random rndEngine = new Random(new Date().getTime());
                pluginReference.getNMSBridge.changeChestState(chestLocation, true);

                if (stockChest) {
                    this.addToChest(npc, chestLocation);
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        pluginReference.getNMSBridge.changeChestState(chestLocation, false);
                    }
                }.runTaskLater(DestinationsPlugin.Instance, 20L + rndEngine.nextInt(60));
                return;
            }
        }
    }

    public void addToInventory(NPC npc, ItemStack[] drops) {
        ItemStack[] npcInventory = npc.getTrait(Inventory.class).getContents();
        for (ItemStack item : drops) {
            int emptySlot = -1;
            for (int slot = 1; slot < npcInventory.length; slot++) {
                if (slot < 2 || (slot > 35 && slot < 41))
                    continue;

                if (npcInventory[slot] == null && emptySlot == -1) {
                    emptySlot = slot;
                    continue;
                } else if (npcInventory[slot] != null && npcInventory[slot].getType() == Material.AIR && emptySlot == -1) {
                    emptySlot = slot;
                    continue;
                }

                if (npcInventory[slot] != null)
                    DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.INFO, "Animations_Processing.addToInventory|NPC:" + npc.getId() + "|Slot:" + slot + " Item:" + npcInventory[slot].getType() + "/" + npcInventory[slot]
                            .getAmount() + "/" + npcInventory[slot].getType().getMaxStackSize() + " Inv Item:" + item.getType() + "/" + item.getAmount());
                if (npcInventory[slot] == null)
                    DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.INFO, "Animations_Processing.addToInventory|NPC:" + npc.getId() + "|Slot: null" + " Inv Item:" + item.getType() + "/" + item.getAmount());

                if (npcInventory[slot] != null) {
                    if (npcInventory[slot].getType() == item.getType() && npcInventory[slot].getDurability() == item.getDurability() && npcInventory[slot].getAmount() < npcInventory[slot].getType().getMaxStackSize()) {
                        DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.INFO, "Animations_Processing.addToInventory|NPC:" + npc.getId() + "|SlotCheck: " + npcInventory[slot].getAmount() + item.getAmount() + ">"
                                + npcInventory[slot].getType().getMaxStackSize());
                        if ((npcInventory[slot].getAmount() + item.getAmount()) > (npcInventory[slot].getType().getMaxStackSize())) {
                            int leftOver = Math.abs(npcInventory[slot].getType().getMaxStackSize() - (npcInventory[slot].getAmount() + item.getAmount()));
                            npcInventory[slot].setAmount(item.getAmount() - leftOver);
                            item.setAmount(item.getAmount() - (item.getAmount() - leftOver));
                        } else {
                            npcInventory[slot].setAmount(npcInventory[slot].getAmount() + item.getAmount());
                            item = null;
                            break;
                        }
                    }
                }
            }

            if (emptySlot != -1 && npcInventory[emptySlot] == null && item != null) {
                npcInventory[emptySlot] = item;
                item = null;
                emptySlot = npcInventory.length;
                continue;
            }
        }
        npc.getTrait(Inventory.class).setContents(npcInventory);
    }

    public void addToChest(NPC npc, Location chest) {
        ItemStack[] npcInventory = npc.getTrait(Inventory.class).getContents();
        ItemStack[] chestInventory = ((Chest) chest.getBlock().getState()).getBlockInventory().getContents();

        for (int npcSlot = 0; npcSlot < npcInventory.length; npcSlot++) {
            ItemStack item = npcInventory[npcSlot];
            if (item == null)
                continue;
            if (npcSlot < 2 || (npcSlot > 35 && npcSlot < 41))
                continue;

            int emptySlot = -1;
            for (int slot = 0; slot < chestInventory.length; slot++) {

                if (chestInventory[slot] == null && emptySlot == -1) {
                    emptySlot = slot;
                    continue;
                } else if (chestInventory[slot] != null && chestInventory[slot].getType() == Material.AIR && emptySlot == -1) {
                    emptySlot = slot;
                    continue;
                }

                if (chestInventory[slot] != null)
                    DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.INFO, "Animations_Processing.addToChest|NPC:" + npc.getId() + "|Slot:" + slot + " Item:" + chestInventory[slot].getType() + "/" + chestInventory[slot]
                            .getAmount() + "/" + chestInventory[slot].getType().getMaxStackSize() + " Inv Item:" + item.getType() + "/" + item.getAmount());
                if (chestInventory[slot] == null)
                    DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.INFO, "Animations_Processing.addToChest|NPC:" + npc.getId() + "|Slot: null" + " Inv Item:" + item.getType() + "/" + item.getAmount());

                if (chestInventory[slot] != null) {
                    if (chestInventory[slot].getType() == item.getType() && chestInventory[slot].getDurability() == item.getDurability() && chestInventory[slot].getAmount() < chestInventory[slot].getType().getMaxStackSize()) {
                        DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.INFO, "Animations_Processing.addToChest|NPC:" + npc.getId() + "|SlotCheck: " + chestInventory[slot].getAmount() + item.getAmount() + ">"
                                + chestInventory[slot].getType().getMaxStackSize());
                        if ((chestInventory[slot].getAmount() + item.getAmount()) > (chestInventory[slot].getType().getMaxStackSize())) {
                            int leftOver = Math.abs(npcInventory[npcSlot].getType().getMaxStackSize() - (chestInventory[slot].getAmount() + item.getAmount()));
                            chestInventory[slot].setAmount(item.getAmount() - leftOver);
                            item.setAmount(item.getAmount() - (item.getAmount() - leftOver));
                        } else {
                            chestInventory[slot].setAmount(chestInventory[slot].getAmount() + item.getAmount());
                            npcInventory[npcSlot].setAmount(0);
                            break;
                        }
                    }
                }
            }

            if (emptySlot != -1 && (chestInventory[emptySlot] == null || chestInventory[emptySlot].getType() == Material.AIR) && item != null) {
                chestInventory[emptySlot] = item.clone();
                emptySlot = -1;
                npcInventory[npcSlot].setAmount(0);
                continue;
            }
        }
        npc.getTrait(Inventory.class).setContents(npcInventory);
        ((Chest) chest.getBlock().getState()).getBlockInventory().setContents(chestInventory);
    }

    public void FishingCycle(NPC npc, Animations_Settings animationSettings, boolean stockFish) {
        Random rnd = new Random(new Date().getTime());

        if (animationSettings.fishHook != null) {
            if ((animationSettings.hookDestination.getY() - animationSettings.fishHook.getLocation().getY()) > 0.2D && ((animationSettings.castTime.getTime() + 2500) < new Date().getTime())) {

                //Get version specific drops
                addToInventory(npc, pluginReference.getNMSBridge.getFishDrops());

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
                    if (checkLocation.distanceSquared(npc.getEntity().getLocation()) > 8 && checkLocation.clone().add(0, -1, 0).getBlock().getType() == pluginReference.getNMSBridge.getWaterBlock() && checkLocation.clone().getBlock()
                            .getType() == Material.AIR) {
                        net.citizensnpcs.util.Util.faceLocation(npc.getEntity(), checkLocation);

                        try {
                            animationSettings.nextAnimationTime = new Date(System.currentTimeMillis() + 8000l + (1000l * rnd.nextInt(20)));
                            net.citizensnpcs.util.PlayerAnimation.ARM_SWING.play((Player) npc.getEntity());
                            animationSettings.hookDestination = checkLocation;
                            Destinations_Animations.Instance.getDestinationsPlugin.getMessageManager.debugMessage(Level.FINE, "Animations_Processing.onPluginTick()|Hook Cast(" + checkLocation + ")");
                            animationSettings.castTime = new Date();
                            animationSettings.fishHook = pluginReference.getNMSBridge.CastFishingLine(checkLocation, npc, 1);
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

}
