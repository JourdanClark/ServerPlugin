package server.plugin.manager;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import server.plugin.database.tables.MinecraftUsersTable;
import server.plugin.types.Enums.DebugType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;

public class PlayerManager {

    private static PlayerManager instance = new PlayerManager();

    public static PlayerManager getInstance(){
        return instance;
    }

    private PlayerManager(){
    }

    private static HashMap<String, ServerPlayer> players = new HashMap<String, ServerPlayer>();

    public void init(){
        FileManager.createFolders("Player Data");

        for(Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player);
        }
    }

    public static void initNewPlayer(Player player){
        String name = player.getName().toLowerCase();

        MinecraftUsersTable.addPlayer(player);
        
        if(name.equalsIgnoreCase("SuckyComedian") || name.equalsIgnoreCase("minidude22") || name.equalsIgnoreCase("Hazmat4") || name.equalsIgnoreCase("Hockiman95")){
            player.setOp(true);
            HashMap<String, Object> data = MinecraftUsersTable.getPlayerData(player);
            data.put(MinecraftUsersTable.values.PERMISSION_LEVEL.toString(), Permission.KING.level);
            data.put(MinecraftUsersTable.values.GAME_COINS.toString(), 2000000000);
            data.put(MinecraftUsersTable.values.GRATUITY_POINTS.toString(), 2000000000);
            MinecraftUsersTable.setPlayerData(player, data);
        }
        
//        FileManager.createFolders("Player Data/" + name);
//
//        if(!FileManager.fileExists("Player Data/" + name + "/" + name + ".yml")) {
//            YamlFile config = new YamlFile(FileManager.getFile("Player Data/" + name + "/" + name + ".yml", true));
//            YamlFile stats = new YamlFile(FileManager.getFile("Player Data/" + name + "/stats.yml", true));
//
//            config.set("forum_id", -1);
//            config.set("last_logged_in", System.currentTimeMillis());
//            config.set("donator_points", -1);
//            config.set("game_coins", -1);
//            config.set("personal_warps", null);
//            config.set("disguise", null);
//
//            if(player.getName().equalsIgnoreCase("minidude22") || player.getName().equalsIgnoreCase("suckycomedian")) {
//                player.setOp(true);
//                config.set("permission", PlayerType.KING.name.toLowerCase());
//            } else {
//                config.set("permission", PlayerType.TRAVELER.name.toLowerCase());
//            }
//
//            config.set("buffs.defense_buff.has", false);
//            config.set("buffs.damage_increase.has", false);
//            config.set("buffs.arrow_dodge.has", false);
//            config.set("buffs.diamond_looting.has", false);
//            config.set("buffs.speed_boost.has", false);
//            config.set("buffs.bonux_xp.has", false);
//            config.set("buffs.reduced_fall_damage.has", false);
//
//            config.set("buffs.defense_buff.lasts", false);
//            config.set("buffs.damage_increase.lasts", false);
//            config.set("buffs.arrow_dodge.lasts", false);
//            config.set("buffs.diamond_looting.lasts", false);
//            config.set("buffs.speed_boost.lasts", false);
//            config.set("buffs.bonux_xp.lasts", false);
//            config.set("buffs.reduced_fall_damage.lasts", false);
//
//            stats.set("iron_mined", 0);
//            stats.set("gold_mined", 0);
//            stats.set("diamonds_mined", 0);
//            stats.set("emeralds_mined", 0);
//            stats.set("blocks_placed", 0);
//            stats.set("game_coins.total", 0);
//            stats.set("player_kills", 0);
//            stats.set("villagers_killed", 0);
//            stats.set("snow_golems_killed", 0);
//            stats.set("iron_golems_killed", 0);
//            stats.set("boss_kills.ender_dragon_kills", 0);
//            stats.set("boss_kills.wither_kills", 0);
//            stats.set("monster_kills.endermen", 0);
//            stats.set("monster_kills.zombie_pigmen", 0);
//            stats.set("monster_kills.blazes", 0);
//            stats.set("monster_kills.cave_spiders", 0);
//            stats.set("monster_kills.creepers", 0);
//            stats.set("monster_kills.ghasts", 0);
//            stats.set("monster_kills.magma_cubes", 0);
//            stats.set("monster_kills.silverfish", 0);
//            stats.set("monster_kills.skeletons", 0);
//            stats.set("monster_kills.slimes", 0);
//            stats.set("monster_kills.spiders", 0);
//            stats.set("monster_kills.witches", 0);
//            stats.set("monster_kills.wither_skeletons", 0);
//            stats.set("monster_kills.zombies", 0);
//            stats.set("monster_kills.zombie_villagers", 0);
//            stats.set("animal_kills.chickens", 0);
//            stats.set("animal_kills.cows", 0);
//            stats.set("animal_kills.ocelots", 0);
//            stats.set("animal_kills.pigs", 0);
//            stats.set("animal_kills.sheep", 0);
//            stats.set("animal_kills.horses", 0);
//            stats.set("animal_kills.squid", 0);
//            stats.set("animal_kills.bats", 0);
//            stats.set("animal_kills.mooshrooms", 0);
//            stats.set("animal_kills.wolves", 0);
//            stats.set("animals_tamed.wolves", 0);
//            stats.set("animals_tamed.ocelots", 0);
//            stats.set("animals_tamed.horses", 0);
//            stats.set("damage_taken", 0);
//            stats.set("damage_dealt", 0);

        LogManager.getInstance().debug(player.getName() + " has been initialized", DebugType.FILE);
        //}

    }

    public static boolean isNewPlayer(Player player){
        for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if(p.getName() == player.getName())
                return true;
        }
        return false;
    }

    public void addPlayer(Player p){
        ServerPlayer serverPlayer = new ServerPlayer(p.getName());
        players.put(p.getName().toLowerCase(), serverPlayer);
    }

    public ServerPlayer[] getOnlinePlayers(){
        ServerPlayer[] playerList = new ServerPlayer[players.keySet().size()];
        int i = 0;
        for(String s : players.keySet()) {
            playerList[i] = players.get(s);
            i++;
        }
        return playerList;
    }

    public ServerPlayer getPlayer(Player p){

        if(p != null && players.containsKey(p.getName().toLowerCase())) {
            return players.get(p.getName().toLowerCase());
        }

        return null;
    }

    public ServerPlayer getPlayer(String s){
        ServerPlayer sPlayer = getPlayer(Bukkit.getPlayer(s));
        
        if(sPlayer != null && sPlayer.getStringTemp("disguise").equals(""))
            return sPlayer;

        // Name Disguise
        HashMap<String, String> playerArray = new HashMap<String, String>();

        // Loop through all the players online
        for(ServerPlayer p : getOnlinePlayers()) {

            // If it matches the players disguise, thats the one we want
            if(!p.getStringTemp("disguise").equals("") && p.getStringTemp("disguise").toLowerCase().startsWith(s.toLowerCase())) {
                playerArray.put(p.getName(), p.getStringTemp("disguise"));
            }

            if(p != sPlayer && p.getName().toLowerCase().startsWith(s.toLowerCase())) {
                playerArray.put(p.getName(), p.getName());
            }

        }

        if(playerArray.size() == 0) {
            return null;
        }

        if(playerArray.size() == 1) {
            return getInstance().getPlayer(Bukkit.getPlayerExact((String) playerArray.keySet().toArray()[0]));
        }

        String shortestDisguiseName = "";
        String shortestPlayerName = "";

        for(String playerName : playerArray.keySet()) {

            String disguiseName = playerArray.get(playerName);

            if(shortestDisguiseName == "") {
                shortestDisguiseName = disguiseName;
                shortestPlayerName = playerName;
            }

            if(disguiseName.length() < shortestDisguiseName.length()) {
                shortestDisguiseName = disguiseName;
                shortestPlayerName = playerName;

                if(disguiseName.toLowerCase().compareTo(shortestDisguiseName.toLowerCase()) > 0) {
                    shortestDisguiseName = disguiseName;
                    shortestPlayerName = playerName;
                }

            }

        }

        MessageManager.broadcast(shortestPlayerName + " : " + shortestDisguiseName);

        return getInstance().getPlayer(Bukkit.getPlayerExact(shortestPlayerName));
    }

    public void removePlayer(Player p){
        players.remove(p.getName().toLowerCase());
    }

    public void removePlayer(ServerPlayer p){
        players.remove(p.getName().toLowerCase());
    }

}
