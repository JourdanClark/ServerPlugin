package server.plugin;

import java.util.ArrayList;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import server.plugin.common.HelperFunctions;
import server.plugin.common.StaticMethods;
import server.plugin.database.MySQL;
import server.plugin.filetype.YamlFile;
import server.plugin.listener.ServerListener;
import server.plugin.manager.ChatChannelManager;
import server.plugin.manager.ConfigManager;
import server.plugin.manager.CooldownManager;
import server.plugin.manager.DataManager;
import server.plugin.manager.FileManager;
import server.plugin.manager.LogManager;
import server.plugin.manager.MenuManager;
import server.plugin.manager.PlayerManager;
import server.plugin.manager.RegionManager;
import server.plugin.manager.WorldManager;
import server.plugin.minigames.MiniGames;
import server.plugin.types.Enums.DebugType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.Task;

import com.carnivalcraft.servermanager.interpreter.Interpreter;

public class ServerPlugin extends JavaPlugin {

    private final int           MINIMUM_AUTOSAVE_DELAY = 30;   // minimum autosave delay is 30 minutes
    public static boolean       freezeTime             = false; // need to move to world manager stuff so we can freeze time in specific worlds
    public static int           freezeID               = -1;
    public static Permission    defaultPermission      = Permission.TRAVELER;
    public static ArrayList<String> blacklist;
    public static YamlFile      config;
    private static ServerPlugin instance;
    

    public static ServerPlugin getInstance(){
        return instance;
    }

    @Override
    public void onEnable(){
        // pass the plugin's folder to the FileManager
        FileManager.dataFolder = getDataFolder().toString();
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ServerListener(), this);
        instance = this;

        initialize();
        MySQL.getInstance().openConnection();
        DataManager.getInstance().init();
        MenuManager.getInstance().init();
        HelperFunctions.init();
        WorldManager.getInstance().init();
        ConfigManager.getInstance().init();
        RegionManager.getInstance().init();
        MiniGames.getInstance().init(this);
        PlayerManager.getInstance().init();
        CooldownManager.getInstance().init();
        ChatChannelManager.getInstance().init();
        ServerListener.getInstance().init();
        Interpreter.getInstance().init();
        LogManager.getInstance().debug("Server Plugin has successfully been initialized!", DebugType.GENERAL);

        // DataManager.getInstance().updateSQL(
        // "INSERT INTO carnival_mcplayer ( web_id, name, game_coins, donator_points, join_date, last_login )"
        // +
        // "VALUE ( 1, 'SuckyComedian', 1, 1, NOW(), NOW() );"
        // );
        // ResultSet set = DataManager.getInstance().querySQL(
        // "SELECT name, game_coins FROM carnival_mcplayer WHERE name = 'mini dude 22';"
        // );
        //
        // try {
        // while( set.next() ){
        // System.out.println(set.getString( "name" ));
        // System.out.println(set.getInt( "game_coins" ));
        //
        // }
        // } catch (SQLException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        
//        DataManager.getInstance().updateSQL("INSERT INTO minecraft_users (name) VALUES ('SuckyComedian')");
//        
//        MinecraftUsersTable.addPlayer(new FakePlayer("SuckyComedian"));
//        HashMap<String, Object> test = MinecraftUsersTable.getPlayerData("SuckyComedian");
//        test.put(MinecraftUsersTable.values.GAME_COINS.toString(), 19990000);
//        test.put(MinecraftUsersTable.values.PERMISSION_LEVEL.toString(), "1000");
//        MinecraftUsersTable.setPlayerData(new FakePlayer("SuckyComedian"), test);
//        
//        ChatChannelsTable.addChannel(1, "Jeffery");
//        System.out.println(
//                MinecraftUsersTable.getPlayerData(
//                        (int) ChatChannelsTable.getChatChannelData("Jeffery")
//                        .get(ChatChannelsTable.values.OWNER_ID.toString()))
//                        .get(MinecraftUsersTable.values.NAME.toString()));
    }

    @Override
    public void onDisable(){
        // for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()){
        // PlayerManager.getInstance().removePlayer(p); //automatically saves
        // all player data
        // }
        StaticMethods.saveWorlds();
        MySQL.getInstance().closeConnection();
    }

    /**
     * Creates the config.yml file in the Config Files folder on start-up.
     */
    private void initialize(){

        FileManager.createFolders("Config Files");

        // create config files
        misc();
        debug();
        autosave();
        permission();
        censor();

        
    }

    private void misc(){
        YamlFile file;
        // Initialize the file if it doesn't exist
        if(!FileManager.fileExists("Config Files/misc.yml")) {
            file = new YamlFile(FileManager.getFile("Config Files/misc.yml", true));
            file.set("tpa.cooldown", 100); // cooldown is 5 seconds
            file.set("disguise_visible_for_us", false);
            LogManager.getInstance().debug("Misc config file initialized.", DebugType.FILE);
        }
    }

    private void debug(){
        YamlFile file;
        // Initialize the file if it doesn't exist
        if(!FileManager.fileExists("Config Files/debug.yml")) {
            file = new YamlFile(FileManager.getFile("Config Files/debug.yml", true));
            file.set("debug.general", false);
            file.set("debug.minigame", false);
            file.set("debug.unknown", false);
            file.set("debug.file", false);
            LogManager.getInstance().debug("Debug config file initialized.", DebugType.FILE);
        }
    }

    private void autosave(){
        YamlFile file;
        // Initialize the file if it doesn't exist
        if(!FileManager.fileExists("Config Files/autosave.yml")) {
            file = new YamlFile(FileManager.getFile("Config Files/autosave.yml", true));
            file.set("silent", true); // whether or not to broadcast messages
            file.set("delay", 60); // does it once an hour
            LogManager.getInstance().debug("Autosave config file initialized.", DebugType.FILE);
        }

        if(!FileManager.fileExists("Config Files/autosave.yml"))
            return;

        file = new YamlFile(FileManager.getFile("Config Files/autosave.yml", false));
        int delay = file.getInt("delay");
        if(delay < MINIMUM_AUTOSAVE_DELAY) {
            LogManager.getInstance().debug("[Autosave] The autosave delay cannot be less than " + MINIMUM_AUTOSAVE_DELAY + " minutes!", DebugType.ERROR);
            LogManager.getInstance().debug("[Autosave] Using " + MINIMUM_AUTOSAVE_DELAY + " instead of " + delay + " for this feature!", DebugType.ERROR);
            delay = MINIMUM_AUTOSAVE_DELAY;
        }
        Task task = new Task();
        task.repeatable(StaticMethods.class, "saveWorlds", (delay * 60) * 20, (delay * 60) * 20);
    }

    private void permission(){
        YamlFile file;
        // Initialize the file if it doesn't exist
        if(!FileManager.fileExists("Config Files/permission.yml")) {
            file = new YamlFile(FileManager.getFile("Config Files/permission.yml", true));
            file.set("disguise_permission", "traveler");
            file.set("permanent_ban_or_jail", "mage");
            LogManager.getInstance().debug("Permission config file initialized.", DebugType.FILE);
        }
    }

    private void censor(){
        YamlFile file;
        // Initialize the file if it doesn't exist
        if(!FileManager.fileExists("Config Files/blacklist.yml")) {
            file = new YamlFile(FileManager.getFile("Config Files/blacklist.yml", true));

        if(!FileManager.fileExists("Config Files/blacklist.yml"))
            return;
        
        file = new YamlFile(FileManager.getFile("Config Files/blacklist.yml", false));
        blacklist = new ArrayList<String>();
        String st = file.toString();
        for(String str : st.split("\n"))
            for(String string : str.split(", "))
                blacklist.add(string);
        
        LogManager.getInstance().debug("Blacklist file initialized.", DebugType.FILE);
        }
    }
}
