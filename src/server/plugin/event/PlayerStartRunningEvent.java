package server.plugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.manager.PlayerManager;

public class PlayerStartRunningEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String                   player;
    protected boolean                cancelled;

    public PlayerStartRunningEvent(Player player){
        this.player = player.getName();
        PlayerManager.getInstance().getPlayer(getPlayer()).setRunning(true);
    }

    public Player getPlayer(){
        return Bukkit.getPlayerExact(player);
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancel){

        cancelled = cancel;
        if(cancel == true)
            PlayerManager.getInstance().getPlayer(getPlayer()).setRunning(false, true);
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
