package server.plugin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import server.plugin.types.Enums.Databases;

/**
 * Connects to and uses a MySQL database
 */
public class MySQL extends Database {
    private final Databases database = Databases.MINECRAFT;
    private String          tag      = "[Database] ";

    private Connection      connection;
    private Statement       statement;

    private static MySQL    instance = new MySQL();

    public static MySQL getInstance(){
        return instance;
    }

    @Override
    public Connection openConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + database.hostName + ":" + database.port + "/" + database.name, database.user, database.password);

            try {
                statement = connection.createStatement();
            } catch(SQLException e) {
                System.out.println("There was an error creating the statement");
            }

        } catch(SQLException e) {
            System.err.println(tag + "Could not connect to MySQL server! because: " + e.getMessage());
        } catch(ClassNotFoundException e) {
            System.err.println(tag + "JDBC Driver not found!");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public boolean checkConnection(){
        return connection != null;
    }

    @Override
    public Connection getConnection(){
        return connection;
    }

    public Statement getStatement(){
        return statement;
    }

    @Override
    public void closeConnection(){
        if(connection != null) {
            try {
                connection.close();
            } catch(SQLException e) {
                System.err.println(tag + "Error while trying to close the MySQL Connection to the database '" + database.name + "'!");
            }
        }
    }

    public Databases getDatabase(){
        return database;
    }
}
