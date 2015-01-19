package server.plugin.database.tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import server.plugin.manager.DataManager;
import server.plugin.types.Enums.DatabaseFieldType;

public class RegionsTable extends DatabaseTable{
    
    public RegionsTable(boolean init){
        super(init);
        name   = "regions";
        
        if(init){
            Field creator_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field world_id = new Field(DatabaseFieldType.TINYINT, "NOT NULL DEFAULT 1");
            Field name = new Field(DatabaseFieldType.CHAR.setLength(30), "NOT NULL");
            Field min_x = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field min_y = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field min_z = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field max_x = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field max_y = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field max_z = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            
            fields.put("world_id", world_id);
            fields.put("creator_id", creator_id);
            fields.put("name", name);
            fields.put("min_x", min_x);
            fields.put("min_y", min_y);
            fields.put("min_z", min_z);
            fields.put("max_x", max_x);
            fields.put("max_y", max_y);
            fields.put("max_z", max_z);
        }
    }
    
    public static int getRegionID(String name){
        return getID(getTableName(), values.NAME.toString(), name);
    }
    
    public static HashMap<String, Object> getRegionData(int id){
        return getData(getTableName(), id, values.toArray());
    }
    
    public static HashMap<String, Object> getRegionData(String world, String name){
        int id = getID(getTableName(), values.WORLD_ID.toString(), WorldsTable.getWorldID(world), values.NAME.toString(), name);
        return getRegionData(id);
    }
    
    public static void setRegionData(String name, HashMap<String, Object> data){
        if(!regionExists(name))
            addRegion((int) data.get(values.CREATOR_ID.toString()), name);
        setData(getTableName(), (int) data.get(values.ID.toString()), data);
    }
    
    public static boolean addRegion(int creatorID, String name){
        if(!regionExists(name)){
            createItem(getTableName(), new String[]{"creator_id","name"}, new Object[]{creatorID, name});
            return true;
        }
        return false;
    }

    public static boolean regionExists(String name){
        return doesExist(getTableName(), values.NAME.toString(), name);
    }
    
    public static String[] getRegionsInWorld(String world){
        ArrayList<String> list = new ArrayList<String>();
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT %s FROM %s WHERE %s='%s'", values.NAME, getTableName(), values.WORLD_ID, WorldsTable.getWorldID(world)));
        try {
            while(set.next()){
                list.add((String) set.getObject(values.NAME.toString()));
            }
            String[] s = new String[list.size()];
            int i = 0;
            for(String region : list){
                s[i] = region;
                i++;
            }
            return s;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
}
    
    public enum values{
        ID("id"),
        CREATOR_ID("creator_id"),
        WORLD_ID("world_id"),
        NAME("name"),
        MIN_X("min_x"),
        MIN_Y("min_y"),
        MIN_Z("min_z"),
        MAX_X("max_x"),
        MAX_Y("max_y"),
        MAX_Z("max_z");
        
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
        return new RegionsTable(false).name;
    }
}
