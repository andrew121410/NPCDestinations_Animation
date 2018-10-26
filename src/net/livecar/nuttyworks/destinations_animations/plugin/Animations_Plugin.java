package net.livecar.nuttyworks.destinations_animations.plugin;

import java.util.logging.Level;

import org.bukkit.Material;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;
import net.livecar.nuttyworks.destinations_animations.Destinations_Animations;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings.enAction;
import net.livecar.nuttyworks.npc_destinations.api.Destination_Setting;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;
import net.livecar.nuttyworks.npc_destinations.plugins.DestinationsAddon;

public class Animations_Plugin extends DestinationsAddon {
    Destinations_Animations pluginReference = null;

    public Animations_Plugin(Destinations_Animations instanceRef) {
        pluginReference = instanceRef;
    }

    @Override
    public String getActionName() {
        return "Animations";
    }

    @Override
    public String getPluginIcon() {
        return "♒";
    }

    @Override
    public String getQuickDescription() {
        String[] response = pluginReference.getDestinationsPlugin.getMessageManager.buildMessage("animations", "messages.plugin_description", "");
        return response[0];
    }

    @Override
    public String getDestinationHelp(NPC npc, NPCDestinationsTrait npcTrait, Destination_Setting location) {
        String[] response = pluginReference.getDestinationsPlugin.getMessageManager.buildMessage("animations", null, "messages.plugin_destination", npcTrait, location, npc, null, 0);
        return response[0];
    }

    @Override
    public String parseLanguageLine(String message, NPCDestinationsTrait npcTrait, Destination_Setting locationSetting, Material blockMaterial, NPC npc, int ident) {
        if (locationSetting != null) {
            if (pluginReference.npcSettings.containsKey(npc.getId())) {
                if (pluginReference.npcSettings.get(npc.getId()).locations.containsKey(locationSetting.LocationIdent)) {
                    if (message.toLowerCase().contains("<animations.setting>")) {
                        enAction npcSetting = pluginReference.npcSettings.get(npc.getId()).locations.get(locationSetting.LocationIdent).action;
                        message = message.replaceAll("<animations\\.setting>", pluginReference.getDestinationsPlugin.getMessageManager.buildMessage("animations", "result_messages." + npcSetting, "")[0]);
                    }
                    if (message.toLowerCase().contains("<animations.argument>")) {
                        message = message.replaceAll("<animations\\.argument>", pluginReference.getDestinationsPlugin.getMessageManager.buildMessage("animations", "result_messages." + pluginReference.npcSettings.get(npc.getId()).locations.get(locationSetting.LocationIdent).arg1, "")[0]);
                    }

                }
            }
        }
        if (message.toLowerCase().contains("<animations.setting>"))
            message = message.replaceAll("<animations\\.setting>", "Not Set");

        return message;
    }

    @Override
    public void onLocationLoading(NPC npc, NPCDestinationsTrait npcTrait, Destination_Setting location, DataKey storageKey) {
        if (!storageKey.keyExists("Animations"))
            return;

        Animations_Settings npcAnimation;
        if (!pluginReference.npcSettings.containsKey(npc.getId())) {
            npcAnimation = new Animations_Settings();
            npcAnimation.setNPC(npc.getId());
            pluginReference.npcSettings.put(npc.getId(), npcAnimation);
        } else {
            npcAnimation = pluginReference.npcSettings.get(npc.getId());
        }

        if (npcAnimation.locations.containsKey(location.LocationIdent))
            npcAnimation.locations.remove(location.LocationIdent);

        Animations_Location locSetting = new Animations_Location();
        
        if (enAction.valueOf(storageKey.getString("Animations.Setting", "")) != null) 
            locSetting.action = enAction.valueOf(storageKey.getString("Animations.Setting", ""));
        
        if (enAction.valueOf(storageKey.getString("Animations.Variable.1", "")) != null) 
            locSetting.arg1 = storageKey.getString("Animations.Variable.1", "");
        
        npcAnimation.locations.put(location.LocationIdent, locSetting);
    }

    @Override
    public void onLocationSaving(NPC npc, NPCDestinationsTrait npcTrait, Destination_Setting location, DataKey storageKey) {
        if (!pluginReference.npcSettings.containsKey(npc.getId()))
            return;
        if (!pluginReference.npcSettings.get(npc.getId()).locations.containsKey(location.LocationIdent))
            return;

        if (pluginReference.npcSettings.get(npc.getId()).locations.containsKey(location.LocationIdent)) 
            storageKey.setString("Animations.Setting", pluginReference.npcSettings.get(npc.getId()).locations.get(location.LocationIdent).action.toString());
        
        if (pluginReference.npcSettings.get(npc.getId()).locations.containsKey(location.LocationIdent)) 
            storageKey.setString("Animations.Variable.1", pluginReference.npcSettings.get(npc.getId()).locations.get(location.LocationIdent).arg1.toString());

        
    }

    @Override
    public void onEnableChanged(NPC npc, NPCDestinationsTrait trait, boolean enabled) {
        if (enabled) {
            if (pluginReference.npcSettings.containsKey(npc.getId())) {
                if (pluginReference.npcSettings.get(npc.getId()).locations.containsKey(trait.currentLocation.LocationIdent)) {
                    if (!pluginReference.monitoredNPC.containsKey(npc.getId())) {

                        pluginReference.getDestinationsPlugin.getMessageManager.debugMessage(Level.INFO, "Animations_Plugin.onNavigationReached|NPC:" + npc.getId() + "|Monitored location reached, assigning as monitor");
                        switch (pluginReference.npcSettings.get(npc.getId()).locations.get(trait.currentLocation.LocationIdent).action) {
                        case NONE:
                        case CHEST:
                        case CHEST_FILL:
                            break;
                        case FISH:
                        case FISH_ADD:
                        case SLEEP:
                        case SWING:
                        case SIT:
                            trait.setMonitoringPlugin(pluginReference.getPluginReference, trait.currentLocation);
                            pluginReference.monitoredNPC.put(npc.getId(), trait.currentLocation.LocationIdent);
                            break;
                        default:
                            break;
                        }
                        return;
                    }
                }
            }
        } else {
            if (pluginReference.monitoredNPC.containsKey(Integer.valueOf(npc.getId()))) {
                pluginReference.getDestinationsPlugin.getMessageManager.debugMessage(Level.INFO, "Animations_Plugin.onNavigationNewDestination|NPC:" + npc.getId() + "|plugin disabled for this npc, removing monitors.");
                trait.unsetMonitoringPlugin();
                pluginReference.monitoredNPC.remove(Integer.valueOf(npc.getId()));
            }
        }
    }

    @Override
    public boolean onNavigationReached(NPC npc, NPCDestinationsTrait trait, Destination_Setting destination) {
        if (pluginReference.npcSettings.containsKey(npc.getId())) {
            if (pluginReference.npcSettings.get(npc.getId()).locations.containsKey(destination.LocationIdent)) {
                if (!pluginReference.monitoredNPC.containsKey(npc.getId())) {
                    pluginReference.getDestinationsPlugin.getMessageManager.debugMessage(Level.INFO, "Animations_Plugin.onNavigationReached|NPC:" + npc.getId() + "|Monitored location reached, assigning as monitor");
                    switch (pluginReference.npcSettings.get(npc.getId()).locations.get(trait.currentLocation.LocationIdent).action) {
                    case CHEST:
                        pluginReference.getProcessingClass.openChest(npc, pluginReference.npcSettings.get(npc.getId()), false);
                        break;
                    case CHEST_FILL:
                        pluginReference.getProcessingClass.openChest(npc, pluginReference.npcSettings.get(npc.getId()), true);
                        break;
                    case FISH:
                    case FISH_ADD:
                    case SLEEP:
                    case SWING:
                    case SIT:
                        // Event triggered
                        trait.setMonitoringPlugin(pluginReference.getPluginReference, destination);
                        pluginReference.monitoredNPC.put(npc.getId(), destination.LocationIdent);
                        break;
                    case NONE:
                    default:
                        break;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onNewDestination(NPC npc, NPCDestinationsTrait trait, Destination_Setting destination) {
        if (pluginReference.npcSettings.containsKey(npc.getId())) {
            if (((Animations_Settings) pluginReference.npcSettings.get(npc.getId())).locations.containsKey(trait.currentLocation.LocationIdent)) {
                if (pluginReference.npcSettings.get(Integer.valueOf(npc.getId())).fishHook != null) {
                    pluginReference.npcSettings.get(Integer.valueOf(npc.getId())).fishHook.remove();
                    pluginReference.npcSettings.get(Integer.valueOf(npc.getId())).fishHook = null;
                }
                
                pluginReference.getProcessingClass.undoAnimations(npc);
                
                if (pluginReference.monitoredNPC.containsKey(Integer.valueOf(npc.getId()))) {
                    pluginReference.getDestinationsPlugin.getMessageManager.debugMessage(Level.INFO, "Animations_Plugin.onNavigationNewDestination|NPC:" + npc.getId() + "|New Location,clearing monitors and releasing control.");
                    trait.unsetMonitoringPlugin();
                    pluginReference.monitoredNPC.remove(Integer.valueOf(npc.getId()));
                }
            }
        }
        return false;
    }
}
