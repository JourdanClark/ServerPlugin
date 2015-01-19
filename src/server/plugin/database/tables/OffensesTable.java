package server.plugin.database.tables;

import server.plugin.types.Enums.DatabaseFieldType;

public class OffensesTable extends DatabaseTable{

    public OffensesTable(boolean init){
        super(init);
        name   = "offenses";
        
        if(init){
            Field player_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field duration = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            Field reason = new Field(DatabaseFieldType.VARCHAR.setLength(255), "NOT NULL");
            Field admin_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field date = new Field(DatabaseFieldType.DATETIME, "NOT NULL DEFAULT NOW()");
            Field action_taken = new Field(DatabaseFieldType.VARCHAR.setLength(255), "NOT NULL");
            
            fields.put("player_id", player_id);
            fields.put("duration", duration);
            fields.put("reason", reason);
            fields.put("admin_id", admin_id);
            fields.put("date", date);
            fields.put("action_taken", action_taken);
        }
    }
    
    public enum Values{
        ID("id"),
        PLAYER_ID("player_id"),
        DURATION("duration"),
        REASON("reason"),
        ADMIN_ID("admin_id"),
        DATE("date"),
        ACTION_TAKEN("action_taken");
        
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
