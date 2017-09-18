package cs4400gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by dayynn on 7/10/17.
 */
public class DatabaseDriver {
    private static Statement stmnt;
    private static Connection conn;

    public static void setupDriver() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cs4400?user=root&useSSL=false");
            stmnt = conn.createStatement();
            System.out.println("Connection to Database created");
        } catch (SQLException ex) {
            // handle any errors
            ex.printStackTrace();
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public static Statement getStmnt() {
        return stmnt;
    }

    public static Connection getConnection() {
        return conn;
    }

}
