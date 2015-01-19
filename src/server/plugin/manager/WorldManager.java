package server.plugin.manager;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class WorldManager {

    private static HashMap<String, String> worlds   = new HashMap<String, String>();

    private static WorldManager            instance = new WorldManager();

    public static WorldManager getInstance(){
        return instance;
    }

    private WorldManager(){
    }

    public boolean createWorld(String worldName, String generator){
        WorldCreator c = new WorldCreator(worldName);

        try {
            c.generator(generator).createWorld();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public World getWorld(String worldName){
        if(worlds.containsKey(worldName.toLowerCase()))
            return Bukkit.getWorld(worlds.get(worldName.toLowerCase()));
        return null;
    }

    /**
     * @return Returns an array of all the worlds on the server.
     */
    public World[] getWorldsAsArray(){
        World[] w = new World[worlds.size()];
        int i = 0;
        for(String s : worlds.keySet()) {
            w[i] = Bukkit.getWorld(s);
            i++;
        }
        return w;
    }

    public HashMap<String, String> getWorlds(){
        return worlds;
    }

    public void init(){
        for(World w : Bukkit.getWorlds()) {
            worlds.put(w.getName().toLowerCase(), w.getName());
        }
    }
}
