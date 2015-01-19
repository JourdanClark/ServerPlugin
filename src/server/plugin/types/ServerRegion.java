package server.plugin.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class ServerRegion {

    public Location min;
    public Location max;
    public String   name;
    public String   world;

    public ServerRegion(String world, String name, Location min, Location max){
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public ServerRegion(String name, Selection sel){
        this.name = name;
        this.min = sel.getMinimumPoint();
        this.max = sel.getMaximumPoint();
        this.world = sel.getWorld().getName();
    }

    public boolean locationInRegion(Location l){
        if(l.getX() >= min.getX() && l.getY() >= min.getY() && l.getZ() >= min.getZ() && l.getX() <= max.getX() && l.getY() <= max.getY() && l.getZ() <= max.getZ())
            return true;
        return false;
    }

    public boolean playerInRegion(Player p){
        Location l = p.getLocation();
        if(l.getX() >= min.getX() && l.getY() >= min.getY() && l.getZ() >= min.getZ() && l.getX() <= max.getX() && l.getY() <= max.getY() && l.getZ() <= max.getZ())
            return true;
        return false;
    }

}
