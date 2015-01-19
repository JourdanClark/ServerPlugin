package server.plugin.database.tables;

import server.plugin.types.Enums.DatabaseFieldType;

public class ActivePerksTable  extends DatabaseTable{
    
    public ActivePerksTable(boolean init){
        super(init);
        name   = "active_perks";
        
        if(init){
            Field perk_id = new Field(DatabaseFieldType.SMALLINT, "NOT NULL");
            Field player_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field time_activated = new Field(DatabaseFieldType.DATETIME, "NOT NULL");
            
            fields.put("perk_id", perk_id);
            fields.put("player_id", player_id);
            fields.put("time_activated", time_activated);
        }
    }
    
    public enum Values{
        ID("id"),
        PERK_ID("perk_id"),
        PLAYER_ID("player_id"),
        TIME_ACTIVATED("time_activated");
        
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