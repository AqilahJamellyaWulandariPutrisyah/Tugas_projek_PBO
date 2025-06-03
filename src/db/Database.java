package db;
import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/concert_ticketing";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}