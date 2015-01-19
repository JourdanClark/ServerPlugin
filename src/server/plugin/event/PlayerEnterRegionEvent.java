package server.plugin.event;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.types.ServerRegion;

public class PlayerEnterRegionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private ServerRegion              region;
    private String                    name;
    private String                    playerName;
    private HashMap<String, Object[]> locations;

    private boolean                   cancel = false;

    public PlayerEnterRegionEvent(Player player, Location from, Location to, Location previousLocation, String name, ServerRegion region){
        this.locations = new HashMap<String, Object[]>();
        this.playerName = player.getName();
        this.name = name;
        this.region = region;

        Object[] locFrom = {from.getWorld().getName(), from.getX(), from.getY(), from.getZ()};

        Object[] locTo = {to.getWorld().getName(), to.getX(), to.getY(), to.getZ()};

        Object[] previousLoc = {previousLocation.getWorld().getName(), previousLocation.getX(), previousLocation.getY(), previousLocation.getZ()};

        locations.put("from", locFrom);
        locations.put("to", locTo);
        locations.put("previous", previousLoc);

    }

    /**
     * @return Returns the location before the event.
     */
    public Location getFrom(){

        return new Location(Bukkit.getServer().getWorld((String) locations.get("from")[0]), (double) locations.get("from")[1], (double) locations.get("from")[2], (double) locations.get("from")[3], getPlayer().getLocation().getYaw(), getPlayer().getLocation().getPitch()

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
     * @return Returns the location saved in the ServerPlayer attributes.
     */
    public Location getPreviousLocation(){

        return new Location(Bukkit.getServer().getWorld((String) locations.get("previous")[0]), (double) locations.get("previous")[1], (double) locations.get("previous")[2], (double) locations.get("previous")[3], getPlayer().getLocation().getYaw(), getPlayer().getLocation().getPitch());
    }

    /**
     * @return Returns the ServerRegion involved in the event.
     */
    public ServerRegion getRegion(){
        return region;
    }

    /**
     * @return Returns the name of the region involved in the event.
     */
    public String getRegionName(){
        return name;
    }

    /**
     * @return Returns the location after the event.
     */
    public Location getTo(){

        return new Location(Bukkit.getServer().getWorld((String) locations.get("to")[0]), (double) locations.get("to")[1], (double) locations.get("to")[2], (double) locations.get("to")[3], getPlayer().getLocation().getYaw(), getPlayer().getLocation().getPitch());
    }

    /**
     * @return Returns true if the event was cancelled.
     */
    public boolean isCancelled(){
        return cancel;
    }

    /**
     * Cancels the event and sends the player to where they were before it.
     * 
     * @param cancel Whether or not to cancel the event.
     */
    public void setCancelled(boolean cancel){
        if(cancel == true) {
            getPlayer().teleport(getPreviousLocation());
            this.cancel = true;
        }
    }

}
