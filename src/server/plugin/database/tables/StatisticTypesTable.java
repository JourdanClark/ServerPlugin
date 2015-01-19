package server.plugin.database.tables;

import java.util.HashMap;

import server.plugin.types.Enums.DatabaseFieldType;
import server.plugin.types.Enums.StatisticType;

public class StatisticTypesTable extends DatabaseTable{

    public StatisticTypesTable(boolean init){
        super(init);
        name   = "statistic_types";
        if(init){
            Field name = new Field(DatabaseFieldType.CHAR + "(50)", "NOT NULL");
            
            fields.put("name", name);
            
            defaultValues = "INSERT INTO " + this.name + " (name) VALUES";
            for(StatisticType stat : StatisticType.values())
               defaultValues += String.format("('%s'),", configureName(stat.name));
            defaultValues = defaultValues.substring(0, defaultValues.length() - 1);
        }
    }
    
    public static int getStatisticTypeID(StatisticType type){
        return getID(getTableName(), values.NAME.toString(), configureName(type.name));
    }
    
    public static HashMap<String, Object> getStatisticTypeData(int id){
        return getData(getTableName(), id, values.toArray());
    }
    
    public static HashMap<String, Object> getStatisticTypeData(StatisticType type){
        return getData(getTableName(), getStatisticTypeID(type), values.toArray());
    }
    
    public static boolean addStatisticType(String name){
        if(!statisticTypeExists(name)){
            createItem(getTableName(), new String[]{"name"}, new Object[]{name});
            return true;
        }
        return false;
    }

    public static boolean statisticTypeExists(String name){
        return doesExist(getTableName(), values.NAME.toString(), name);
    }
    
    public enum values{
        ID("id"),
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

    private static String configureName(String name){
        return name.toLowerCase().replace(" ", "_");
    }
    
    private static String getTableName(){
        return new StatisticTypesTable(false).name;
    }
}
