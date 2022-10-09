package classes.dao;


import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "monitoring";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static String conn = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME;
    private Connection connection;
    private static Logger log = Logger.getLogger(DBConnection.class.getName());

    public DBConnection() {
        try {
            connection = DriverManager.getConnection(conn, USERNAME, PASSWORD);
        } catch (SQLException e) {
            log.log(Level.WARNING, "EXCEPTION!: ", e);
        }
    }


    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.log(Level.WARNING, "EXCEPTION!: ", e);
        }
    }

}
