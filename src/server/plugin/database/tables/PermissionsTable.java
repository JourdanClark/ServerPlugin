package server.plugin.database.tables;

import java.util.HashMap;

import org.bukkit.entity.Player;

import server.plugin.ServerPlugin;
import server.plugin.types.Enums.DatabaseFieldType;
import server.plugin.types.Enums.Permission;

public class PermissionsTable extends DatabaseTable{

    public PermissionsTable(boolean init){
        super(init);
        name   = "permissions";
        
        if(init){
            Field level = new Field(DatabaseFieldType.SMALLINT, "NOT NULL");
            Field name = new Field(DatabaseFieldType.CHAR.setLength(20), "NOT NULL");
            
            fields.put("level", level);
            fields.put("name", name);
            
            defaultValues = "INSERT INTO " + this.name + " (level, name) VALUES";
            for(Permission perm : Permission.values())
               defaultValues += String.format("(%s,'%s'),", perm.level, perm.name.toLowerCase());
            defaultValues = defaultValues.substring(0, defaultValues.length() - 1);
        }
    }
    
    public static int getPermissionID(String name){
        return getID(getTableName(), values.NAME.toString(), name);
    }
    
    public static String getPermissionName(int id){
        return (String) getObject(getTableName(), id, values.NAME.toString());
    }
    
    public static Permission getPlayerPermission(Player player){
        return getPlayerPermission(player.getName());
    }
    
    public static Permission getPlayerPermission(String name){
        int level = ServerPlugin.defaultPermission.level;
        try{
            level = (int)MinecraftUsersTable.getPlayerData(name).get(MinecraftUsersTable.values.PERMISSION_LEVEL.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return Permission.getPermission(level);
    }
    
    public static boolean setPlayerPermission(Player player, Permission permission){
        return setPlayerPermission(player.getName(), permission);
    }
    
    public static boolean setPlayerPermission(String name, Permission permission){
        HashMap<String, Object> data = MinecraftUsersTable.getPlayerData(name);
        Permission playerPerm = Permission.getPermission((int) data.get(MinecraftUsersTable.values.PERMISSION_LEVEL.toString()));
        if(playerPerm == Permission.KING)
            return false;
        data.put(MinecraftUsersTable.values.PERMISSION_LEVEL.toString(), permission.level);
        MinecraftUsersTable.setPlayerData(name, data);
        return true;
    }
    
    public enum values{
        ID("id"),
        LEVEL("level"),
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
        return new PermissionsTable(false).name;
    }
}
