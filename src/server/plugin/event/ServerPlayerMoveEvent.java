package server.plugin.event;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.manager.PlayerManager;

public class ServerPlayerMoveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private Boolean                   canceled = false;
    private String                    playerName;

    private HashMap<String, Object[]> locations;

    public ServerPlayerMoveEvent(Player player, Location from, Location to){
        this.locations = new HashMap<String, Object[]>();
        this.playerName = player.getName();

        Object[] locFrom = {from.getWorld().getName(), from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch()};

        Object[] locTo = {to.getWorld().getName(), to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch()};

        locations.put("from", locFrom);
        locations.put("to", locTo);
    }

    /**
     * @return Returns the Block Location before the event.
     */
    public Location getBlockFrom(){
        return getFrom().getBlock().getLocation();
    }

    /**
     * @return Returns the Block Location after the event.
     */
    public Location getBlockTo(){
        return getTo().getBlock().getLocation();
    }

    /**
     * @return Returns the Location before the event.
     */
    public Location getFrom(){

        return new Location(Bukkit.getServer().getWorld((String) locations.get("from")[0]), (double) locations.get("from")[1], (double) locations.get("from")[2], (double) locations.get("from")[3], (float) locations.get("from")[4], (float) locations.get("from")[5]

        );
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    /**
     * @return Returns the Player involved in the event.
     */
    public Player getPlayer(){
        return Bukkit.getServer().getPlayerExact(playerName);
    }

    /**
     * @return Returns the ServerPlayer Location saved in the ServerPlayer
     *         attributes.
     */
    public Location getPreviousLocation(){
        return PlayerManager.getInstance().getPlayer(getPlayer()).getPreviousBlockLocation();
    }

    /**
     * @return Returns the Location after the event.
     */
    public Location getTo(){

        return new Location(Bukkit.getServer().getWorld((String) locations.get("to")[0]), (double) locations.get("to")[1], (double) locations.get("to")[2], (double) locations.get("to")[3], (float) locations.get("to")[4], (float) locations.get("to")[5]

        );
    }

    /**
     * @return Returns true if the event was cancelled.
     */
    public boolean isCancelled(){
        return canceled;
    }

    /**
     * Cancels the event if true.
     * 
     * @param cancel Whether or not to cancel the event.
     */
    public void setCanceled(Boolean cancel){
        canceled = cancel;
    }

}
