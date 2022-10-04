package classes;

import java.sql.*;

public class DBConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "monitoring";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static String conn = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME;
    private Connection connection;

    public DBConnection() {
        try {
            connection = DriverManager.getConnection(conn, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
