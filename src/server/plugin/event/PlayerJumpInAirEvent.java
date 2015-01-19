package server.plugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJumpInAirEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String                   player;

    public PlayerJumpInAirEvent(Player player){
        this.player = player.getName();

    }

    public Player getPlayer(){
        return Bukkit.getPlayerExact(player);
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
