package net.livecar.nuttyworks.destinations_animations.plugin;

import java.util.logging.Level;

import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import net.citizensnpcs.api.npc.NPC;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings.enAction;
import net.livecar.nuttyworks.npc_destinations.DestinationsPlugin;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;
import net.livecar.nuttyworks.npc_destinations.listeners.commands.CommandInfo;
import net.livecar.nuttyworks.npc_destinations.api.Destination_Setting;

public class Animations_Commands {
    @CommandInfo(name = "locanimate", 
            group = "External Plugin Commands", 
            languageFile = "animations", 
            helpMessage = "command_locanimate_help", 
            arguments = { "#", "sleep|chest|chest_fill|fish|fish_add|sit|swing|clear" }, 
            permission = {"npcdestinations.editall.locanimate", "npcdestinations.editown.locanimate" }, 
            allowConsole = true, 
            minArguments = 2, 
            maxArguments = 3)
    public boolean npcDest_locAnimation(DestinationsPlugin destRef, CommandSender sender, NPC npc, String[] inargs, boolean isOwner, NPCDestinationsTrait destTrait) {

        if (inargs.length < 2) {
            destRef.getMessageManager.sendMessage("animations", sender, "messages.command_badargs");
            return true;
        }

        int nIndex = Integer.parseInt(inargs[1]);
        if (nIndex > destTrait.NPCLocations.size() - 1) {
            destRef.getMessageManager.sendMessage("animations", sender, "messages.command_badargs");
            return true;
        }

        Animations_Plugin addonReference = (Animations_Plugin) destRef.getPluginManager.getPluginByName("Animations");
        Destination_Setting destSetting = destTrait.NPCLocations.get(nIndex);
        Animations_Settings animSetting = null;

        if (!addonReference.pluginReference.npcSettings.containsKey(npc.getId())) {
            animSetting = new Animations_Settings();
            animSetting.setNPC(npc.getId());
            addonReference.pluginReference.npcSettings.put(npc.getId(), animSetting);
        } else {
            animSetting = addonReference.pluginReference.npcSettings.get(npc.getId());
        }

        if (inargs.length == 2) {
            // Remove the settings, and detach this from the location.
            if (addonReference.pluginReference.monitoredNPC.containsKey(npc.getId())) {
                if (addonReference.pluginReference.monitoredNPC.get(npc.getId()).equals(destSetting.LocationIdent)) {
                    addonReference.pluginReference.getDestinationsPlugin.getMessageManager.debugMessage(Level.INFO, "Animations_Plugin.onNavigationNewDestination|NPC:" + npc.getId()
                            + "|New Location,clearing monitors and releasing control.");
                    destTrait.unsetMonitoringPlugin();
                    addonReference.pluginReference.npcSettings.get(npc.getId()).currentDestination = null;
                    addonReference.pluginReference.monitoredNPC.remove(npc.getId());
                }
            }
            if (animSetting.locations.containsKey(destSetting.LocationIdent)) {
                animSetting.locations.remove(destSetting.LocationIdent);
                addonReference.pluginReference.getDestinationsPlugin.getMessageManager.sendMessage("animations", sender, "messages.command_removed", destTrait, destSetting);
            }
            return true;
        }

        if (EnumUtils.isValidEnum(enAction.class, inargs[2].toUpperCase())) {
            if (animSetting.locations.containsKey(destSetting.LocationIdent))
                animSetting.locations.remove(destSetting.LocationIdent);

            Animations_Location locArg = new Animations_Location();

            locArg.action = enAction.valueOf(inargs[2].toUpperCase());
            if (inargs.length > 3)
                locArg.arg1 = inargs[3];

            animSetting.locations.put(destSetting.LocationIdent, locArg);

            if (addonReference.pluginReference.monitoredNPC.containsKey(npc.getId()))
                addonReference.pluginReference.monitoredNPC.remove(npc.getId());

            addonReference.pluginReference.getDestinationsPlugin.getMessageManager.sendMessage("animations", sender, "messages.command_added_active", destTrait, destSetting, Material.AIR);
            if (destSetting.LocationIdent.equals(destTrait.currentLocation.LocationIdent) && !addonReference.pluginReference.monitoredNPC.containsKey(npc.getId()))
                addonReference.pluginReference.monitoredNPC.put(npc.getId(), destSetting.LocationIdent);
            return true;
        } else if (inargs[2].equalsIgnoreCase("clear")) {
            if (animSetting.locations.containsKey(destSetting.LocationIdent))
                animSetting.locations.remove(destSetting.LocationIdent);
            if (addonReference.pluginReference.monitoredNPC.containsKey(npc.getId()))
                addonReference.pluginReference.monitoredNPC.remove(npc.getId());
            addonReference.pluginReference.getDestinationsPlugin.getMessageManager.sendMessage("animations", sender, "messages.command_removed", destTrait, destSetting, Material.AIR);
            return true;
        } else {
            addonReference.pluginReference.getDestinationsPlugin.getMessageManager.sendMessage("animations", sender, "messages.command_badargs", destTrait, destSetting);
            return true;
        }
    }
}
