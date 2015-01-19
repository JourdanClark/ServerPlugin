package server.plugin.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import server.plugin.database.MySQL;
import server.plugin.database.tables.ActivePerksTable;
import server.plugin.database.tables.ChatChannelsTable;
import server.plugin.database.tables.DatabaseTable;
import server.plugin.database.tables.DisguisesTable;
import server.plugin.database.tables.GratuityPerksTable;
import server.plugin.database.tables.HousesTable;
import server.plugin.database.tables.LocationsTable;
import server.plugin.database.tables.MinecraftUsersTable;
import server.plugin.database.tables.OffensesTable;
import server.plugin.database.tables.PermissionsTable;
import server.plugin.database.tables.RegionsTable;
import server.plugin.database.tables.StatisticTypesTable;
import server.plugin.database.tables.StatisticsTable;
import server.plugin.database.tables.WorldsTable;
import server.plugin.types.Enums.Databases;

public class DataManager extends MySQL {

    public static DataManager instance = new DataManager();

    public void init(){
        createTable(new MinecraftUsersTable(true), false);
        createTable(new PermissionsTable(true), false);
        createTable(new StatisticsTable(true), false);
        createTable(new StatisticTypesTable(true), false);
        createTable(new OffensesTable(true), false);
        createTable(new HousesTable(true), false);
        createTable(new GratuityPerksTable(true), false);
        createTable(new ActivePerksTable(true), false);
        createTable(new ChatChannelsTable(true), false);
        createTable(new LocationsTable(true), false);
        createTable(new RegionsTable(true), false);
        createTable(new WorldsTable(true), false);
        createTable(new DisguisesTable(true), false);
    }
    
    public void createTable(DatabaseTable table, boolean overwrite){
        if(!overwrite){
            ResultSet result = querySQL("SELECT COUNT(*) AS count FROM information_schema.tables WHERE table_schema = '" + Databases.MINECRAFT.name + "' AND table_name = '" + table.getName() + "'");
            try {
                result.next();
                if(result.getInt("count") > 0){
                    return;
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        String statement = "CREATE TABLE " + table.getName() + "(";
        Object[] set = table.getFields().keySet().toArray();
        for(int i=0;i<table.getFields().size();i++){
            statement += set[i] + " " + table.getFields().get(set[i]).getType() + " " + table.getFields().get(set[i]).getValue();
            if(i == table.getFields().size() - 1)
                statement += ", PRIMARY KEY(id));";
            else
                statement += ", ";
        }
        updateSQL(statement);
        if(table.getDefaultValuesStatement() != null)
            updateSQL(table.getDefaultValuesStatement());
       LogManager.getInstance().info(String.format("Table '%s' created in '%s'", table.getName(), Databases.MINECRAFT.name));
    }

    public static DataManager getInstance(){
        return instance;
    }

    public ResultSet querySQL(String query){

        if(!query.endsWith(";"))
            query += ";";
        
        Statement s = MySQL.getInstance().getStatement();

        ResultSet rs = null;

        try {
            rs = s.executeQuery(query);
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("\n\n\n" + query + "\n\n\n");
        }

        return rs;
    }

    public void updateSQL(String update){

        if(!update.endsWith(";"))
            update += ";";
        if(update.contains("='false'"))
            update = update.replaceAll("='false'", "=0");
        if(update.contains("='true'"))
            update = update.replaceAll("='true'", "=1");

        Statement s = MySQL.getInstance().getStatement();

        try {
            s.executeUpdate(update);
        } catch(SQLException e1) {
            e1.printStackTrace();
            System.out.println("\n\n\n" + update + "\n\n\n");
        }

    }

}
