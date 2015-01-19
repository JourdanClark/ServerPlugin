package server.plugin.database.tables;

import server.plugin.types.Enums.DatabaseFieldType;

public class HousesTable extends DatabaseTable{

    public HousesTable(boolean init){
        super(init);
        name   = "houses";
        
        if(init){
            Field player_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field x_coord = new Field(DatabaseFieldType.BIGINT, "NOT NULL");
            Field z_coord = new Field(DatabaseFieldType.BIGINT, "NOT NULL");
            Field plot_size = new Field(DatabaseFieldType.SMALLINT, "NOT NULL DEFAULT 20");
            Field purchase_date = new Field(DatabaseFieldType.DATETIME, "NOT NULL DEFAULT NOW()");
            
            fields.put("player_id", player_id);
            fields.put("x_coord", x_coord);
            fields.put("z_coord", z_coord);
            fields.put("plot_size", plot_size);
            fields.put("purchase_date", purchase_date);
        }
    }
    
    public enum Values{
        ID("id"),
        PLAYER_ID("player_id"),
        X_COORD("x_coord"),
        Z_COORD("z_coord"),
        PLOT_SIZE("plot_size"),
        PURCHASE_DATE("purchase_date");
        
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
