package server.plugin.database.tables;

import java.util.HashMap;

import server.plugin.types.Enums.DatabaseFieldType;

public class ChatChannelsTable extends DatabaseTable{
    
    public ChatChannelsTable(boolean init){
        super(init);
        name   = "chat_channels";
        
        if(init){
            Field owner_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field permission = new Field(DatabaseFieldType.SMALLINT, "NOT NULL DEFAULT 200");
            Field name = new Field(DatabaseFieldType.VARCHAR.setLength(20), "NOT NULL");
            
            fields.put("owner_id", owner_id);
            fields.put("permission", permission);
            fields.put("name", name);
        }
    }
    
    public static int getChatChannelID(String name){
        return getID(getTableName(), values.NAME.toString(), name);
    }
    
    public static HashMap<String, Object> getChatChannelData(int id){
        return getData(getTableName(), id, values.toArray());
    }
    
    public static HashMap<String, Object> getChatChannelData(String name){
        return getData(getTableName(), getChatChannelID(name), values.toArray());
    }
    
    public static void setChannelData(String name, HashMap<String, Object> data){
        if(!channelExists(name))
            addChannel((int) data.get(values.OWNER_ID.toString()), name);
        setData(getTableName(), (int) data.get(values.ID.toString()), data);
    }
    
    public static boolean addChannel(int ownerID, String name){
        if(!channelExists(name)){
            createItem(getTableName(), new String[]{"owner_id","name"}, new Object[]{ownerID, name});
            return true;
        }
        return false;
    }

    public static boolean channelExists(String name){
        return doesExist(getTableName(), values.NAME.toString(), name);
    }
    
    public enum values{
        ID("id"),
        OWNER_ID("owner_id"),
        PERMISSION("permission"),
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
        return new ChatChannelsTable(false).name;
    }
}