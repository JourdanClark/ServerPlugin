package server.plugin.database.tables;

import java.util.HashMap;

import org.bukkit.entity.Player;

import server.plugin.types.Enums.DatabaseFieldType;

public class MinecraftUsersTable extends DatabaseTable{
    
    public MinecraftUsersTable(boolean init){
        super(init);
        name   = "minecraft_users";
        
        if(init){
            Field web_id          = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL DEFAULT -1");
            Field name            = new Field(DatabaseFieldType.CHAR.setLength(16), "NOT NULL");
            Field game_coins      = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field gratuity_points = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field join_date       = new Field(DatabaseFieldType.DATETIME, "NOT NULL DEFAULT  NOW()");
            Field permission_level   = new Field(DatabaseFieldType.SMALLINT, "NOT NULL DEFAULT 200");
            Field last_login      = new Field(DatabaseFieldType.DATETIME, "NOT NULL DEFAULT NOW()");
            Field disguise_id     = new Field(DatabaseFieldType.TINYINT, "NOT NULL DEFAULT -1");
            Field banned          = new Field(DatabaseFieldType.BIT, "NOT NULL DEFAULT 0");
        
            fields.put("web_id", web_id);
            fields.put("name", name);
            fields.put("game_coins", game_coins);
            fields.put("gratuity_points", gratuity_points);
            fields.put("join_date", join_date);
            fields.put("permission_level", permission_level);
            fields.put("last_login", last_login);
            fields.put("disguise_id", disguise_id);
            fields.put("banned", banned);    
        }
    }
    
    public static int getPlayerID(String name){
        return getID(getTableName(), values.NAME.toString(), name);
    }
    
    public static int getPlayerID(Player player){
        return getPlayerID(player.getName());
    }
    
    public static HashMap<String, Object> getPlayerData(int id){
        return getData(getTableName(), id, values.toArray());
    }
    
    public static HashMap<String, Object> getPlayerData(Player player){
        return getData(getTableName(), getPlayerID(player), values.toArray());
    }
    
    public static HashMap<String, Object> getPlayerData(String name){
        return getData(getTableName(), getPlayerID(name), values.toArray());
    }

    public static void setPlayerData(Player player, HashMap<String, Object> data){
        if(!playerExists(player))
            addPlayer(player);
        setData(getTableName(), (int) data.get(values.ID.toString()), data);
    }
    
    public static void setPlayerData(String name, HashMap<String, Object> data){
        if(!playerExists(name))
            addPlayer(name);
        setData(getTableName(), (int) data.get(values.ID.toString()), data);
    }
    
    public static boolean addPlayer(Player player){
        if(!playerExists(player)){
            createItem(getTableName(), new String[]{"name"}, new Object[]{player.getName()});
            return true;
        }
        return false;
    }
    
    public static boolean addPlayer(String name){
        if(!playerExists(name)){
            createItem(getTableName(), new String[]{"name"}, new Object[]{name});
            return true;
        }
        return false;
    }
    
    public static boolean playerExists(Player player){
        return doesExist(getTableName(), values.NAME.toString(), player.getName());
    }
    
    public static boolean playerExists(String name){
        return doesExist(getTableName(), values.NAME.toString(), name);
    }
    
    public enum values{
        ID("id"),
        WEB_ID("web_id"),
        NAME("name"),
        GAME_COINS("game_coins"),
        GRATUITY_POINTS("gratuity_points"),
        JOIN_DATE("join_date"),
        PERMISSION_LEVEL("permission_level"),
        LAST_LOGIN("last_login"),
        DISGUISE_ID("disguise_id"),
        BANNED("banned");
        
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
        return new MinecraftUsersTable(false).name;
    }
}
