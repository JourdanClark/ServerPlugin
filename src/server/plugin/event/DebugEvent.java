package server.plugin.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.types.Enums.DebugType;

public class DebugEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private String    message;
    private DebugType type   = DebugType.UNKNOWN;

    private boolean   cancel = false;

    public DebugEvent(String message){
        this.message = message;
    }

    public DebugEvent(String message, DebugType type){
        this.message = message;
        this.type = type;
    }

    /**
     * @return Returns the DebugType involved in the event.
     */
    public DebugType getDebugType(){
        return type;
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    /**
     * @return Returns the message involved in the event.
     */
    public String getMessage(){
        return message;
    }

    /**
     * @return Returns true if the event is cancelled.
     */
    public boolean isCancelled(){
        return cancel;
    }

    /**
     * Cancels the event if true.
     * 
     * @param cancel Whether or not to cancel the event.
     */
    public void setCancelled(boolean cancel){
        if(cancel == true)
            this.cancel = true;
    }
}