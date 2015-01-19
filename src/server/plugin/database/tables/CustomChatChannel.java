package server.plugin.database.tables;

import server.plugin.types.Enums.DatabaseFieldType;

public class CustomChatChannel extends DatabaseTable{
    
    public CustomChatChannel(int channelID, String name){
        super(true);
        this.name   = "channel_" + name;
        
        Field channel_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL DEFAULT " + channelID);
        Field allowed_player_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
        
        fields.put("channel_id", channel_id);
        fields.put("allowed_player_id", allowed_player_id);
    }
    
    public enum Values{
        ID("id"),
        CHANNEL_ID("channel_id"),
        ALLOWED_PLAYER_ID("allowed_player_id");
        
        public String name;
        Values(String name){
            this.name = name;
        }
        
        @Override
        public String toString(){
            return name;
        }
    }
}