package server.plugin.manager;

import java.util.HashMap;

import org.bukkit.entity.Player;

import server.plugin.types.TeleportRequest;

public class TeleportManager {

    private static TeleportManager instance = new TeleportManager();

    public static TeleportManager getInstance(){
        return instance;
    }

    public TeleportManager(){
    }

    private HashMap<String, TeleportRequest> tpaRequests = new HashMap<String, TeleportRequest>();

    /**
     * Adds a TPA request to the HashMap.
     */
    public void addTPARequest(Player receivesRequest, TeleportRequest request){
        tpaRequests.put(receivesRequest.getName(), request);
    }

    /**
     * @return Returns the HashMap containing all TPA requests.
     */
    public HashMap<String, TeleportRequest> getTPARequests(){
        return tpaRequests;
    }

    /**
     * @param player The player the request is for.
     * @return Returns the tpa request for the given player.
     */
    public TeleportRequest getTPARequest(Player player){
        return tpaRequests.get(player.getName());
    }

    /**
     * Creates a tpa request.
     * 
     * @param player The player that will be teleported.
     * @param playerTo The player that 'player' is teleporting to.
     * @return Returns the created tpa request.
     */
    public TeleportRequest createTPARequest(Player player, Player playerTo){
        return new TeleportRequest(player, playerTo);
    }

    public void removeTPARequest(Player player){
        if(tpaRequests.containsKey(player.getName()))
            tpaRequests.remove(player.getName());
    }
}
