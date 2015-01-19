package server.plugin.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.types.ServerRegion;

public class PlayerLeaveRegionEvent extends PlayerEnterRegionEvent {

    public PlayerLeaveRegionEvent(Player player, Location from, Location to, Location previousLocation, String name, ServerRegion region){
        super(player, from, to, previousLocation, name, region);
    }
}
