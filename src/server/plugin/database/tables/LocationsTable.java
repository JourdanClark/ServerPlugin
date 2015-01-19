package server.plugin.database.tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import server.plugin.manager.DataManager;
import server.plugin.types.Enums.DatabaseFieldType;

public class LocationsTable extends DatabaseTable{
    
    public LocationsTable(boolean init){
        super(init);
        name   = "locations";

        if(init){
            Field creator_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field name = new Field(DatabaseFieldType.CHAR.setLength(20), "NOT NULL");
            Field world_id = new Field(DatabaseFieldType.TINYINT, "NOT NULL DEFAULT 1");
            Field x = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field y = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field z = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field pitch = new Field(DatabaseFieldType.DOUBLE, "NOT NULL DEFAULT 0.0");
            Field yaw = new Field(DatabaseFieldType.DOUBLE, "NOT NULL DEFAULT 0.0");
            
            fields.put("creator_id", creator_id);
            fields.put("world_id", world_id);
            fields.put("name", name);
            fields.put("x", x);
            fields.put("y", y);
            fields.put("z", z);
            fields.put("pitch", pitch);
            fields.put("yaw", yaw);
        }
    }
    
    public static int getLocationID(String world, String name){
        return getID(getTableName(), values.WORLD_ID.toString(), WorldsTable.getWorldID(world), values.NAME.toString(), name);
    }
    
    public static HashMap<String, Object> getLocationData(int id){
        return getData(getTableName(), id, values.toArray());
    }
    
    public static HashMap<String, Object> getLocationData(String world, String name){
        return getData(getTableName(), getLocationID(world, name), values.toArray());
    }
   
    public static String[] getLocationsInWorld(String world){
            ArrayList<String> list = new ArrayList<String>();
            ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT %s FROM %s WHERE %s='%s'", values.NAME, getTableName(), values.WORLD_ID, WorldsTable.getWorldID(world)));
            try {
                while(set.next()){
                    list.add((String) set.getObject(values.NAME.toString()));
                }
                String[] s = new String[list.size()];
                int i = 0;
                for(Object string : list.toArray()){
                    s[i] = (String) string;
                    i++;
                }
                return s;
            } catch(SQLException e) {
                e.printStackTrace();
            }
            return null;
    }
   
    public static void setLocationData(HashMap<String, Object> data){
        HashMap<String, Object> d = WorldsTable.getWorldData((int) data.get(values.WORLD_ID.toString()));
        if(!locationExists((String) d.get(WorldsTable.values.NAME.toString()), (String) data.get(values.NAME.toString())))
            addLocation((int) data.get(values.CREATOR_ID.toString()), (String) d.get(WorldsTable.values.NAME.toString()), (String) data.get(values.NAME.toString()));
        setData(getTableName(), (int) data.get(values.ID.toString()), data);
    }
    
    public static boolean addLocation(int creatorID, String world, String name){
        if(!locationExists(world, name)){
            createItem(getTableName(), new String[]{"creator_id","name"}, new Object[]{creatorID, name});
            return true;
        }
        return false;
    }

    public static boolean locationExists(String world, String name){
        return doesExist(getTableName(), values.NAME.toString(), name, values.WORLD_ID.toString(), WorldsTable.getWorldID(world));
    }
    
    public enum values{
        ID("id"),
        CREATOR_ID("creator_id"),
        NAME("name"),
        WORLD_ID("world_id"),
        X("x"),
        Y("y"),
        Z("z"),
        PITCH("pitch"),
        YAW("yaw");
        
        public String name;
        values(String name){
            this.name = name;
        }
        
        public static String[] toArray(){
            String[] array = new String[values.values().length];
            int i = 0;
            for(values v : values.values()){
                array[i] = v.toString();
                i++;
            }
            return array;
        }
        
        @Override
        public String toString(){
            return name;
        }
    }

    private static String getTableName(){
        return new LocationsTable(false).name;
    }
}
