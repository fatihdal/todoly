package dal.fatih.todoly;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    static String userName = "sa";
    static String password = "sa";
    static String dbUrl = "jdbc:h2:~/todoly_db";

    public Connection getConnection() throws SQLException {

        return DriverManager.getConnection(dbUrl, userName, password);
    }
}