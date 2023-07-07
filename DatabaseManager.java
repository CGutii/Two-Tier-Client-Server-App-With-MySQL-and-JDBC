/*
Name: Cristian Gutierrez
Course: CNT 4714 Summer 2023
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 2, 2023
Class: Enterprise Computing CNT 4714-23Summer C001
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseManager {
    private Connection connection;
    private Connection operationsLogConnection;
    
    public void connect(String dbPropFileName, String userPropFileName, String username, String password) throws SQLException, IOException, ClassNotFoundException {
        // Load database properties
        Properties dbProps = new Properties();
        try (FileInputStream dbPropFile = new FileInputStream("properties/"+dbPropFileName)) {
            dbProps.load(dbPropFile);
        }

        // Load user properties
        Properties userProps = new Properties();
        try (FileInputStream userPropFile = new FileInputStream("properties/"+userPropFileName)) {
            userProps.load(userPropFile);
        }
        
//        // Load sensitive properties
//        Properties sensitiveProps = new Properties();
//        try (FileInputStream sensitivePropFile = new FileInputStream("properties/sensitive.properties")) {
//            sensitiveProps.load(sensitivePropFile);
//        }
        
        String driverClass = dbProps.getProperty("MYSQL_DB_DRIVER_CLASS");
        String dbUrl = dbProps.getProperty("MYSQL_DB_URL");

        // Load and register the driver
        Class.forName(driverClass);

        // Create a connection using the loaded properties
        connection = DriverManager.getConnection(dbUrl, username, password);
        
        // Connect to operations log
        String opsLogDbUrl = "jdbc:mysql://localhost:3306/operationslog?useTimezone=true&serverTimezone=UTC";
        //String rootPassword = sensitiveProps.getProperty("MYSQL_DB_PASSWORD");
        operationsLogConnection = DriverManager.getConnection(opsLogDbUrl, "project2app", "project2app");
    }

    public boolean executeSQLCommand(String sqlCommand) throws SQLException {
        Statement statement = connection.createStatement();
        boolean isResultSet = statement.execute(sqlCommand);
        updateOperationsLog(isResultSet);
        return isResultSet;
    }
    
    public Connection getConnection() {
        return connection;
    }

    public void updateOperationsLog(boolean isQuery) throws SQLException {
        // query to get current counts
        Statement statement = operationsLogConnection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM operationscount");
        resultSet.next();
        int numQueries = resultSet.getInt("num_queries");
        int numUpdates = resultSet.getInt("num_updates");
        resultSet.close();
        
        // update the appropriate count
        if(isQuery) {
            numQueries++;
        } else {
            numUpdates++;
        }
        
        // update the counts in the operationslog
        Statement updateStatement = operationsLogConnection.createStatement();
        updateStatement.executeUpdate("UPDATE operationscount SET num_queries = " + numQueries + ", num_updates = " + numUpdates);
        updateStatement.close();
    }
}
