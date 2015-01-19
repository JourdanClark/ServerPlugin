package server.plugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import server.plugin.event.DebugEvent;
import server.plugin.types.Enums.DebugType;

public class LogManager {

    // private ConfigManager manager = ConfigManager.getInstance();
    // private YamlFile config = manager.getConfig("Config Files.config");

    private static LogManager instance = new LogManager();

    private static String     prefix   = "[ServerPlugin] ";

    public static LogManager getInstance(){
        return instance;
    }

    private LogManager(){
    }

    public boolean debug(String message){
        DebugEvent event = new DebugEvent(ChatColor.UNDERLINE + "" + ChatColor.ITALIC + "[Debug] " + message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(isAllowed(DebugType.UNKNOWN)) {
            if(!event.isCancelled()) {
                System.out.println("[Debug] " + message);
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean debug(String message, DebugType type){
        DebugEvent event = new DebugEvent(ChatColor.UNDERLINE + "" + ChatColor.ITALIC + "[Debug] " + message, type);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(isAllowed(type)) {
            if(!event.isCancelled()) {
                if(type == DebugType.ERROR) {
                    System.err.println("[Debug] " + message);
                    return true;
                }
                System.out.println("[Debug] " + message);
                return true;
            }
            return false;
        }
        return false;
    }

    public void info(String msg){
        Bukkit.getLogger().info(prefix + msg);
    }

    private boolean isAllowed(DebugType type){
        if(type == DebugType.GENERAL) {
            return isDebugGeneral();
        }
        if(type == DebugType.MINIGAME) {
            return isDebugMinigame();
        }
        if(type == DebugType.UNKNOWN) {
            return isDebugUnknown();
        }
        return false;
    }

    public boolean isDebugFile(){
        return ConfigManager.getInstance().getConfig("Config Files.debug").getBoolean("debug.file");
    }

    public boolean isDebugGeneral(){
        return ConfigManager.getInstance().getConfig("Config Files.debug").getBoolean("debug.general");
    }

    public boolean isDebugMinigame(){
        return ConfigManager.getInstance().getConfig("Config Files.debug").getBoolean("debug.minigame");
    }

    public boolean isDebugUnknown(){
        return ConfigManager.getInstance().getConfig("Config Files.debug").getBoolean("debug.unknown");
    }

    public void warning(String msg){
        Bukkit.getLogger().warning(prefix + msg);
    }

}
