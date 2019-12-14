package net.livecar.nuttyworks.destinations_animations;

import net.citizensnpcs.Citizens;
import net.livecar.nuttyworks.destinations_animations.nms.VersionBridge;
import net.livecar.nuttyworks.destinations_animations.plugin.Destinations_Plugin;
import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings;
import net.livecar.nuttyworks.destinations_animations.plugin.Commands;
import net.livecar.nuttyworks.destinations_animations.plugin.Processing;
import net.livecar.nuttyworks.npc_destinations.DestinationsPlugin;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Destinations_Animations {
    public DestinationsPlugin                getDestinationsPlugin = null;

    // For quick reference to this instance of the plugin.
    public static Destinations_Animations    Instance              = null;

    // Links to classes
    public Citizens                          getCitizensPlugin     = null;

    public VersionBridge                     getNMSBridge         = null;

    public DestAnimations_Plugin             getPluginReference    = null;
    public Processing                        getProcessingClass    = null;
    public Destinations_Plugin               getAnimationPlugin    = null;

    // variables
    public int                               entityRadius          = 47 * 47;

    public Map<Integer, Animations_Settings> npcSettings           = new HashMap<Integer, Animations_Settings>();
    public Map<Integer, UUID>                monitoredNPC          = new HashMap<Integer, UUID>();

    // Storage locations
    public File                              languagePath;

    public Destinations_Animations() {
        this.getAnimationPlugin = new Destinations_Plugin(this);
        DestinationsPlugin.Instance.getPluginManager.registerPlugin(getAnimationPlugin);
        DestinationsPlugin.Instance.getCommandManager.registerCommandClass(Commands.class);
        this.getProcessingClass = new Processing(this);
    }

    void getDefaultConfigs() {
        // Create the default folders
        if (!DestinationsPlugin.Instance.getDataFolder().exists())
            DestinationsPlugin.Instance.getDataFolder().mkdirs();
        if (!languagePath.exists())
            languagePath.mkdirs();

        // Validate that the default package is in the MountPackages folder. If
        // not, create it.
        exportConfig(languagePath, "en_def-animations.yml");
    }

    private void exportConfig(File path, String filename) {
        DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.FINEST, "nuDestinationsAnimations.exportConfig()|");
        File fileConfig = new File(path, filename);
        if (!fileConfig.isDirectory()) {
            // Reader defConfigStream = null;
            try {
                FileUtils.copyURLToFile((URL) getClass().getResource("/" + filename), fileConfig);
            } catch (IOException e1) {
                if (getDestinationsPlugin != null)
                    DestinationsPlugin.Instance.getMessageManager.debugMessage(Level.SEVERE, "nuDestinationsAnimations.exportConfig()|FailedToExtractFile(" + filename + ")");
                else
                    logToConsole(" Failed to extract default file (" + filename + ")");
                return;
            }
        }
    }

    public void logToConsole(String logLine) {
        Bukkit.getLogger().log(java.util.logging.Level.INFO, "[" + DestinationsPlugin.Instance.getDescription().getName() + "] " + logLine);
    }

}
