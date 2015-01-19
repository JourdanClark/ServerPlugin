package server.plugin.minigames.arena;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.filetype.YamlFile;
import server.plugin.manager.FileManager;
import server.plugin.manager.LocationManager;
import server.plugin.manager.LogManager;
import server.plugin.manager.RegionManager;
import server.plugin.types.ServerRegion;

public class ArenaManager {

    private static ArenaManager instance = new ArenaManager();

    public static ArenaManager getInstance(){
        return instance;
    }

    private String                 basePath = "minigames/arenas";

    private HashMap<String, Arena> arenas   = new HashMap<String, Arena>();

    private ArenaManager(){
    }

    public boolean addArena(Player player, ServerRegion region, String arenaName){
        return createArena(region, player.getWorld().getName(), arenaName, new String[]{"default"});
    }

    public boolean addArena(Player player, ServerRegion region, String arenaName, String[] teams){
        return createArena(region, player.getWorld().getName(), arenaName, teams);
    }

    private boolean createArena(ServerRegion region, String regionWorld, String arenaName, String[] teams){
        FileManager.createFolders(basePath + "/" + regionWorld);

        if(FileManager.fileExists(basePath + "/" + regionWorld + "/" + arenaName + ".yml"))
            return false;

        YamlFile config = new YamlFile(FileManager.getFile(basePath + "/" + regionWorld + "/" + arenaName + ".yml", true));

        int arenaID = arenas.size();

        Arena arena = new Arena(arenaID, arenaName, region, teams);

        arenas.put(arenaName.toLowerCase(), arena);

        config.set("region.name", region.name);
        config.set("region.world", regionWorld);

        for(int i = 0;i < teams.length;i++) {
            if(config.get("teams.ids") == null) {
                config.set("teams.ids", i);
            } else {
                config.set("teams.ids", config.getString(arenaName + ".teams.ids") + "|" + i);
            }

            config.set("teams." + i + ".name", teams[i]);
            config.set("teams." + i + ".spawn", "CHANGE_ME_TO_A_LOCATION_NAME");
        }

        return true;
    }

    public Arena getArena(Player p){
        for(String arena : arenas.keySet()) {
            if(arenas.get(arena).containsPlayer(p))
                return arenas.get(arena);
        }

        return null;
    }

    public Arena getArena(String name){
        if(arenas.containsKey(name.toLowerCase()))
            return arenas.get(name.toLowerCase());

        return null;
    }

    public HashMap<String, Arena> getArenas(){
        return arenas;
    }

    public void initialize(){
        FileManager.createFolders(basePath);

        // Loop through all the worlds
        for(File directory : FileManager.getFolderFolders(basePath)) {

            LogManager.getInstance().info(basePath + "/" + directory.getName());

            // Loop through all the arena's
            for(File arena : FileManager.getFolderFiles(basePath + "/" + directory.getName())) {
                String arenaName = FilenameUtils.removeExtension(arena.getName());

                YamlFile config = new YamlFile(FileManager.getFile(basePath + "/" + directory.getName() + "/" + arenaName + ".yml", false));

                String[] team_ids;
                if(config.getString("teams.ids").contains("|")) {
                    team_ids = config.getString("teams.ids").split("|");
                } else {
                    team_ids = new String[]{config.getString("teams.ids")};
                }

                // team location
                HashMap<String, Location> teams = new HashMap<String, Location>();

                for(int i2 = 0;i2 < team_ids.length;i2++) {
                    String team = config.getString("teams." + i2 + ".name");

                    // skip bad stuffs
                    if(team == null || team.contains("|") || !(team.length() > 0))
                        continue;

                    teams.put(team, LocationManager.getInstance().getLocation(config.getString("region.world"), config.getString("teams." + i2 + ".spawn")));
                }

                // create the object
                Arena a = new Arena(arenas.size(), arenaName, RegionManager.getInstance().getRegion(config.getString("region.world"), config.getString("region.name")), teams.keySet().toArray(new String[teams.size()]));

                // add object to the list
                arenas.put(arenaName, a);

                // set the spawns to what they were supposed to be
                for(String team : teams.keySet()) {
                    a.setSpawnLocation(team, teams.get(team));
                }
            }
        }
    }
}
