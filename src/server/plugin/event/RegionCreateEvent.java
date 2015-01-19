package server.plugin.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.types.ServerRegion;

public class RegionCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private ServerRegion region;

    private boolean      isCancelled = false;

    public RegionCreateEvent(ServerRegion Region){
        region = Region;
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    /**
     * @return Returns the created ServerRegion.
     */
    public ServerRegion getRegion(){
        return region;
    }

    /**
     * @return Returns true if the event was canceled.
     */
    public boolean isCancelled(){
        return isCancelled;
    }

    /**
     * Stops the ServerRegion from being created.
     * 
     * @param cancelled Whether or not to cancel the event.
     */
    public void setCancelled(boolean cancelled){
        isCancelled = cancelled;
    }
}
