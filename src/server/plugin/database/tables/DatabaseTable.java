package server.plugin.database.tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import server.plugin.manager.DataManager;
import server.plugin.types.Enums.DatabaseFieldType;

public abstract class DatabaseTable {

    HashMap<String, Field> fields;
    String name;
    String defaultValues;
    
    public DatabaseTable(boolean init){
        if(init){
            fields   = new HashMap<String, Field>();
            Field id = new Field(DatabaseFieldType.MEDIUMINT, "NOT NULL AUTO_INCREMENT");
            fields.put("id", id);
        }
    }
    
    public String getName(){
        return name;
    }
    
    public HashMap<String, Field> getFields(){
        return fields;
    }
    
    public String getDefaultValuesStatement(){
        return defaultValues;
    }
    
    public enum values{
        VALUE("name");
        
        public String name;
        values(String name){
            this.name = name;
        }
        
        @Override
        public String toString(){
            return name;
        }
    }

    protected static HashMap<String, Object> getData(String table, int id, String[] values){
        if(id < 0)
            return null;
        HashMap<String, Object> result = new HashMap<String, Object>();
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT * FROM %s WHERE id='%s'", table, id));
        try {
            set.next();
            for(String s : values)
                result.put(s, set.getObject(s));
            return result;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected static Object getObject(String table, int id, String key){
        if(id < 0)
            return null;
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT %s FROM %s WHERE id='%s'", key, table, id));
        try {
            set.next();
            return set.getObject(key);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected static void setData(String table, int key, HashMap<String, Object> data){
        String statement = "UPDATE " + table + " SET";
        Set<String> set = data.keySet();
        for(String s : set)
            statement += String.format(" %s='%s',", s, data.get(s));
        statement = statement.substring(0, statement.length() - 1);
        statement += " WHERE id=" + data.get("id");
        DataManager.getInstance().updateSQL(statement);
    }
    
    protected static int getID(String table, String key, Object value){
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT id FROM %s WHERE %s='%s'", table, key, value));
        try {
            set.next();
            return set.getInt("id");
        } catch(SQLException e) {
            return -1;
        }
    }
    
    protected static int getID(String table, String key, Object value, String andKey, Object andValue){
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT id FROM %s WHERE %s='%s' AND %s='%s'", table, key, value, andKey, andValue));
        try {
            set.next();
            return set.getInt("id");
        } catch(SQLException e) {
            return -1;
        }
    }
    
    protected static void createItem(String table, String[] keys, Object[] values){
        String s = "(", s2 = "(";
        
        for(String str : keys)
            s += String.format("%s,", str);
        s = s.substring(0, s.length() - 1) + ")";
        
        for(Object str : values)
            s2 += String.format("'%s',", str);
        s2 = s2.substring(0, s2.length() - 1) + ")";
        
        DataManager.getInstance().updateSQL(String.format("INSERT INTO %s %s VALUES %s", table, s, s2));
    }
    
    protected static boolean doesExist(String table, String key, Object value){
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT COUNT(*) AS count FROM %s WHERE %s='%s'", table, key, value));
        try {
            set.next();
            if(set.getInt("count") > 0)
                return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    protected static boolean doesExist(String table, String key, Object value, String andKey, Object andValue){
        ResultSet set = DataManager.getInstance().querySQL(String.format("SELECT COUNT(*) AS count FROM %s WHERE %s='%s' AND %s='%s'", table, key, value, andKey, andValue));
        try {
            set.next();
            if(set.getInt("count") > 0)
                return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void removeData(DatabaseTable table, String[] keys, Object[] values){
        String statement = String.format("DELETE FROM %s WHERE ", table);
        for(String k : keys)
            for(Object v : values)
                statement += String.format("%s='%s' AND", k, v);
        statement = statement.substring(0, statement.length() - 4);
        DataManager.getInstance().updateSQL(statement);
    }

}
