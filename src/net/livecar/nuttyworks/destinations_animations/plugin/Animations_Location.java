package net.livecar.nuttyworks.destinations_animations.plugin;

import java.util.Date;

import org.bukkit.Sound;

import net.livecar.nuttyworks.destinations_animations.plugin.Animations_Settings.enAction;

public class Animations_Location {
    public enAction action = enAction.NONE;
    public int interval = 500;
    public Sound sound = null;
    public enSoundCategory soundCategory = null; 
    public float volume = 1.0F;
    public float pitch = 0.0F;
    public Date lastAction = new Date();
    
    public enum enSoundCategory
    {
        AMBIENT  ,
        BLOCKS   ,
        HOSTILE  ,
        MASTER   ,
        MUSIC    ,
        NEUTRAL  ,
        PLAYERS  ,
        RECORDS  ,
        VOICE    ,
        WEATHER  ,
    }
    
}
