package server.plugin.database.tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import server.plugin.manager.DataManager;
import server.plugin.types.Enums.DatabaseFieldType;

public class StatisticsTable extends DatabaseTable{
    
    public StatisticsTable(boolean init){
        super(init);
        name   = "statistics";
        if(init){
            Field statistic_id = new Field(DatabaseFieldType.SMALLINT, "NOT NULL");
            Field player_id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL");
            Field count = new Field(DatabaseFieldType.BIGINT, "NOT NULL DEFAULT 0");
            
            fields.put("statistic_id", statistic_id);
            fields.put("player_id", player_id);
            fields.put("count", count);
        }
    }
    
    public static int getStatisticID(int statisticTypeID, int playerID){
        if(!statDataExists(statisticTypeID, playerID))
            createItem(getTableName(), new String[]{"statistic_id", "player_id", "count"}, new Object[]{statisticTypeID, playerID, 0});
            
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT id FROM %s WHERE %s='%s' AND %s='%s'", 
                getTableName(), values.STATISTIC_ID.toString(), statisticTypeID, values.PLAYER_ID.toString(), playerID));
        try {
            set.next();
            return set.getInt("id");
        } catch(SQLException e) {
            return -1;
        }
    }
    
    public static HashMap<String, Object> getStatisticData(int id){
        return getData(getTableName(), id, values.toArray());
    }
    
    public static void setStatisticData(HashMap<String, Object> data){
        setData(getTableName(), (int) data.get(values.ID.toString()), data);
    }
    
    public static boolean statDataExists(int statisticTypeID, int playerID){
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT id FROM %s WHERE %s='%s' AND %s='%s'", 
                getTableName(), values.STATISTIC_ID.toString(), statisticTypeID, values.PLAYER_ID.toString(), playerID));
        try {
            set.next();
            if(set.getObject("id") != null)
                return true;
        } catch(SQLException e) {
            return false;
        }
        return false;
    }

    public enum values{
        ID("id"),
        STATISTIC_ID("statistic_id"),
        PLAYER_ID("player_id"),
        COUNT("count");
        
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
        return new StatisticsTable(false).name;
    }
}
