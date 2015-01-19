package server.plugin.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.database.tables.RegionsTable;
import server.plugin.database.tables.WorldsTable;
import server.plugin.event.RegionCreateEvent;
import server.plugin.types.ServerRegion;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class RegionManager {
    public static RegionManager getInstance(){
        return instance;
    }

    private WorldEditPlugin                               worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

    // world name region
    public ArrayList<ServerRegion> regions   = new ArrayList<ServerRegion>();

    private static RegionManager                          instance  = new RegionManager();

    private RegionManager(){
    }

    public void init(){
        String[] worlds = WorldsTable.getAllWorlds();
        for(String world : worlds){
            String[] regions = RegionsTable.getRegionsInWorld(world);
            for(String region : regions){
                HashMap<String, Object> data = RegionsTable.getRegionData(world, region);
                int min_x = (int) data.get(RegionsTable.values.MIN_X.toString());
                int min_y = (int) data.get(RegionsTable.values.MIN_Y.toString());
                int min_z = (int) data.get(RegionsTable.values.MIN_Z.toString());
                int max_x = (int) data.get(RegionsTable.values.MAX_X.toString());
                int max_y = (int) data.get(RegionsTable.values.MAX_Y.toString());
                int max_z = (int) data.get(RegionsTable.values.MAX_Z.toString());
                Location min_loc = new Location(Bukkit.getWorld(world), min_x, min_y, min_z);
                Location max_loc = new Location(Bukkit.getWorld(world), max_x, max_y, max_z);
                ServerRegion r = new ServerRegion(world, (String) data.get(RegionsTable.values.NAME.toString()), min_loc, max_loc);
                this.regions.add(r);
            }
        }
    }
    
    public boolean createRegion(Player player, String name, Selection selection){
        ServerRegion region = new ServerRegion(name, selection);
        RegionCreateEvent event = new RegionCreateEvent(region);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if(!event.isCancelled()) {
            RegionsTable.addRegion(PlayerManager.getInstance().getPlayer(player).getID(), name);
            HashMap<String, Object> data = RegionsTable.getRegionData(selection.getWorld().getName(), name);
            
            data.put(RegionsTable.values.WORLD_ID.toString(), WorldsTable.getWorldID(selection.getWorld().getName()));
            data.put(RegionsTable.values.MIN_X.toString(), region.min.getX());
            data.put(RegionsTable.values.MIN_Y.toString(), region.min.getY());
            data.put(RegionsTable.values.MIN_Z.toString(), region.min.getZ());
            data.put(RegionsTable.values.MAX_X.toString(), region.max.getX());
            data.put(RegionsTable.values.MAX_Y.toString(), region.max.getY());
            data.put(RegionsTable.values.MAX_Z.toString(), region.max.getZ());

            regions.add(region);
                return true;
            }
        return false;
    }

    public ServerRegion[] getLocationInWorldRegions(Location loc){
        if(regions.isEmpty())
            return null;
        String world = loc.getWorld().getName();

        ArrayList<ServerRegion> return_regions = new ArrayList<ServerRegion>();

        for(ServerRegion region : regions) {
            if(region.world == world && region.locationInRegion(loc))
                return_regions.add(region);
        }

        ServerRegion[] r = new ServerRegion[return_regions.size()];
        int i = 0;
        for(ServerRegion region : return_regions){
            r[i] = region;
            i++;
        }
        return r;
    }

    public ServerRegion[] getPlayerInWorldRegions(Player player){
        return getLocationInWorldRegions(player.getLocation());
    }

    public Selection getPlayerSelection(Player p){
        return worldEdit.getSelection(p);
    }

    public ServerRegion getRegion(String world, String name){
        for(ServerRegion region : regions){
            if(region.name.equalsIgnoreCase(world))
                return region;
        }
        return null;
    }

    public boolean removeRegion(String world, String name){
        if(regions.isEmpty())
            return false;
        for(ServerRegion region : regions){
            if(region.name.equalsIgnoreCase(name)){
                if(region.world.equalsIgnoreCase(world)){
                    RegionsTable.removeData(
                            new RegionsTable(false), 
                            new String[]{RegionsTable.values.WORLD_ID.toString(), RegionsTable.values.NAME.toString()},
                            new Object[]{WorldsTable.getWorldID(world), name}
                            );
                    regions.remove(region);
                    return true;
                }
            }
        }
        return false;
    }

}
