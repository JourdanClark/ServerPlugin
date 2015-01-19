package server.plugin.manager;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.common.HelperFunctions;
import server.plugin.database.tables.LocationsTable;
import server.plugin.database.tables.WorldsTable;

public class LocationManager {

    private static LocationManager instance = new LocationManager();

    public static LocationManager getInstance(){
        return instance;
    }

    private LocationManager(){
    }

    public boolean createLocation(Player player, String name, Location location){
        String world = location.getWorld().getName();

        if(!LocationsTable.locationExists(world, name)){
            LocationsTable.addLocation(PlayerManager.getInstance().getPlayer(player).getID(), world, name);
            HashMap<String, Object> data = LocationsTable.getLocationData(LocationsTable.getLocationID(world, name));
            
            data.put(LocationsTable.values.WORLD_ID.toString(), WorldsTable.getWorldID(world));
            data.put(LocationsTable.values.X.toString(), location.getX());
            data.put(LocationsTable.values.Y.toString(), location.getY());
            data.put(LocationsTable.values.Z.toString(), location.getZ());
            data.put(LocationsTable.values.PITCH.toString(), location.getPitch());
            data.put(LocationsTable.values.YAW.toString(), location.getYaw());
            
            LocationsTable.setLocationData(data);
        }
        return false;
    }

    public Location getLocation(String world, String name){

        if(LocationsTable.locationExists(world, name)) {
            HashMap<String, Object> data = LocationsTable.getLocationData(world, name);
            if(data != null){
                int worldID = (int) data.get(LocationsTable.values.WORLD_ID.toString());
                String w = (String) WorldsTable.getWorldData(worldID).get(WorldsTable.values.NAME.toString());
                int x = (int) data.get(LocationsTable.values.X.toString());
                int y = (int) data.get(LocationsTable.values.Y.toString());
                int z = (int) data.get(LocationsTable.values.Z.toString());
                double pitch = (double) data.get(LocationsTable.values.PITCH.toString());
                double yaw = (double) data.get(LocationsTable.values.YAW.toString());
                
                return HelperFunctions.createLocationFromPoints(new Object[]{w,x,y,z,pitch,yaw});
            }
        }
        return null;
    }

    public String[] getWorldLocations(String world){
        return LocationsTable.getLocationsInWorld(world);
    }

    public boolean removeLocation(String world, String name){
        if(LocationsTable.locationExists(world, name)){
            HashMap<String, Object> data = LocationsTable.getLocationData(world, name);
            Object[] values = new Object[data.keySet().size()];
            int i = 0;
            for(String s : data.keySet()){
                values[i] = data.get(s);
                i++;
            }
            LocationsTable.removeData(new LocationsTable(false), LocationsTable.values.toArray(), values);
            return true;
        }
        return false;
    }

    public boolean setLocation(String world, String name, Location location){

        if(LocationsTable.locationExists(world, name)) {
            HashMap<String, Object> data = LocationsTable.getLocationData(LocationsTable.getLocationID(world, name));

            data.put(LocationsTable.values.WORLD_ID.toString(), WorldsTable.getWorldID(world));
            data.put(LocationsTable.values.X.toString(), location.getX());
            data.put(LocationsTable.values.Y.toString(), location.getY());
            data.put(LocationsTable.values.Z.toString(), location.getZ());
            data.put(LocationsTable.values.PITCH.toString(), location.getPitch());
            data.put(LocationsTable.values.YAW.toString(), location.getYaw());
            
            LocationsTable.setLocationData(data);
            return true;
        }
        return false;
    }
}
