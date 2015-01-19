package server.plugin.database.tables;

import server.plugin.types.Enums.DatabaseFieldType;

public class GratuityPerksTable extends DatabaseTable{

    //TODO need to make enum for perks
    
    public GratuityPerksTable(boolean init){
        super(init);
        name   = "gratuity_perks";
        
        if(init){
            Field name = new Field(DatabaseFieldType.CHAR.setLength(40), "NOT NULL");
            Field duration = new Field(DatabaseFieldType.INT, "NOT NULL DEFAULT 0");
            
            fields.put("name", name);
            fields.put("duration", duration);
        }
    }
    
    public enum Values{
        ID("id"),
        NAME("name"),
        DURATION("duration");
        
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
