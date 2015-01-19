package server.plugin.database.tables;

import server.plugin.types.Enums.DatabaseFieldType;

public class Field {

    String type, value;
    
    public Field(DatabaseFieldType type, String value){
        this.type = type.toString();
        this.value = value;
    }
    
    public Field(String type, String value){
        this.type = type;
        this.value = value;
    }
    
    public String getType(){
        return type;
    }
    
    public String getValue(){
        return value;
    }
}
