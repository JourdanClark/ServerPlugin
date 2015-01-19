package server.plugin.minigames;

import server.plugin.ServerPlugin;
import server.plugin.minigames.arena.ArenaManager;

public class MiniGames {

    private static MiniGames instance = new MiniGames();

    public static MiniGames getInstance(){
        return instance;
    }

    // private ServerPlugin plugin;

    private MiniGames(){
    }

    public void init(ServerPlugin plugin){
        // this.plugin = plugin;

        ArenaManager.getInstance().initialize();

        // initializeRedVsBlue();
    }

    // public void initializeRedVsBlue() {
    // plugin.getCommand("redvsblue").setExecutor( new RedVsBlueCommandManager()
    // );
    // }
    //
}
