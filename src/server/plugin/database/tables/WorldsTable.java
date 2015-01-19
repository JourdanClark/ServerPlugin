package server.plugin.database.tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

import server.plugin.manager.DataManager;
import server.plugin.types.Enums.DatabaseFieldType;

public class WorldsTable extends DatabaseTable{
    
    public WorldsTable(boolean init){
        super(init);
        name   = "worlds";
        if(init){
            Field name = new Field(DatabaseFieldType.CHAR.setLength(20), "NOT NULL");
            fields.put("name", name);
            
            defaultValues = "INSERT INTO " + this.name + " (name) VALUES";
            for(World world : Bukkit.getWorlds())
               defaultValues += String.format("('%s'),", world.getName());
            defaultValues = defaultValues.substring(0, defaultValues.length() - 1);
        }
    }
    
    public static int getWorldID(String name){
        return getID(getTableName(), values.NAME.toString(), name);
    }
    
    public static HashMap<String, Object> getWorldData(int id){
        return getData(getTableName(), id, values.toArray());
    }
    
    public static HashMap<String, Object> getWorldData(String name){
        return getData(getTableName(), getWorldID(name), values.toArray());
    }
    
    public static boolean addWorld(String name){
        if(!worldExists(name)){
            createItem(getTableName(), new String[]{"name"}, new Object[]{name});
            return true;
        }
        return false;
    }

    public static boolean worldExists(String name){
        return doesExist(getTableName(), values.NAME.toString(), name);
    }
    
    public static String[] getAllWorlds(){
        ArrayList<String> worlds = new ArrayList<String>();
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT * FROM %s", getTableName()));
        try {
            while(set.next())
                worlds.add(set.getString(values.NAME.toString()));
            String[] s = new String[worlds.size()];
            int i = 0;
            for(String world : worlds){
                s[i] = world;
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
        NAME("name");
        
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
        return new WorldsTable(false).name;
    }
}
