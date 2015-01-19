package server.plugin.database.tables;

import java.util.HashMap;

import org.bukkit.entity.Player;

import server.plugin.types.Enums.DatabaseFieldType;

public class DisguisesTable extends DatabaseTable{
    
    public DisguisesTable(boolean init){
        super(init);
        name   = "disguises";
        
        if(init){
            Field player_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field name = new Field(DatabaseFieldType.CHAR.setLength(16), "NOT NULL");
            
            fields.put("player_id", player_id);
            fields.put("name", name);
        }
    }
    
    public static int getDisguiseID(String name){
        return getID(getTableName(), values.NAME.toString(), name);
    }
    
    public static int getDisguiseID(Player player){
        return getID(getTableName(), values.PLAYER_ID.toString(), player.getName());
    }
    
    public static HashMap<String, Object> getDisguiseData(int id){
        return getData(getTableName(), id, values.toArray());
    }
    
    public static HashMap<String, Object> getDisguiseData(Player player){
        return getData(getTableName(), getDisguiseID(player), values.toArray());
    }
    
    public static HashMap<String, Object> getDisguiseData(String name){
        return getData(getTableName(), getDisguiseID(name), values.toArray());
    }
    
    public static boolean addDisguise(int playerID, String name){
        if(!disguiseExists(name)){
            createItem(getTableName(), new String[]{"player_id","name"}, new Object[]{playerID, name});
            return true;
        }
        return false;
    }

    public static boolean disguiseExists(String name){
        return doesExist(getTableName(), values.NAME.toString(), name);
    }
    
    public enum values{
        ID("id"),
        PLAYER_ID("player_id"),
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
        return new DisguisesTable(false).name;
    }
}
