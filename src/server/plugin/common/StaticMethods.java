package server.plugin.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import server.plugin.filetype.YamlFile;
import server.plugin.manager.ConfigManager;
import server.plugin.manager.WorldManager;

public class StaticMethods {

    // This class is meant for static methods that you are using for tasks that
    // don't really belong anywhere else.

    /**
     * Sets time in the world.
     * @param world The world to be effected.
     * @param time The time to set it to.
     */
    public static void setTime(World world, long time){
        world.setTime(time);
    }

    /**
     * Saves all the worlds.
     */
    public static void saveWorlds(){
        YamlFile file = ConfigManager.getInstance().getConfig("Config Files.autosave");
        if(file == null)
            return;
        boolean silent = file.getBoolean("silent");
        if(!silent)
            Bukkit.getServer().broadcastMessage(ChatColor.BOLD + "The server is saving. Expect some lag.");
        for(World world : WorldManager.getInstance().getWorldsAsArray()) {
            world.save();
            if(!silent)
                System.out.println(world.getName() + " has been saved!");
        }
        if(!silent)
            Bukkit.getServer().broadcastMessage(ChatColor.BOLD + "All worlds have been saved");
    }

}
