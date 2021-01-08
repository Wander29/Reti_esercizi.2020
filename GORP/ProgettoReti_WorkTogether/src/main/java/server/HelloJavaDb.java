package server;

import utils.psw.PasswordManager;
import utils.psw.PswData;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

public class HelloJavaDb {
    /*
    Derby implements an SQL-92 core subset, as well as some SQL-99 features.
     */
    // -------------------------------------------
    // URL format is
    // jdbc:derby:<local directory to save data>
    // -------------------------------------------
    private static final String dbUrl = "jdbc:derby:memory:test/db;create=true";
    private static Connection conn = null;

    public static void main(String[] args) throws SQLException {
        HelloJavaDb app = new HelloJavaDb();


        //
        try {
            app.normalDbUsage();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");

        } catch (SQLException ex) {
            if (ex.getSQLState().equals("XJ015")) {
                System.out.println("Derby shutdown normally");
            } else {
                // could not shutdown the database
                // handle appropriately
            }
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void normalDbUsage() throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        conn = DriverManager.getConnection(dbUrl);
        Statement stmt = conn.createStatement();

        // drop table
        /*
        stmt.executeUpdate("DROP TABLE Users");
        stmt.executeUpdate("DROP TABLE ProjectMembers");
        stmt.executeUpdate("DROP TABLE Projects");
        stmt.executeUpdate("DROP TABLE MulticastIP");
        stmt.executeUpdate("DROP TABLE Cards");
        stmt.executeUpdate("DROP TABLE Movements");
         */


        stmt.executeUpdate(
            "CREATE TABLE Users(" +
                "ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                "Name VARCHAR(50), " +
                "Password VARCHAR(128) FOR BIT DATA," +
                "Salt VARCHAR(128) FOR BIT DATA)");

        // index improves performance
        stmt.executeUpdate("CREATE UNIQUE INDEX nameIndex ON users (Name)");

        // insert 1 row
        PreparedStatement pstmnt = conn.prepareStatement("" +
            "INSERT INTO users (Name, Password, Salt) values (?, ?, ?)");
        pstmnt.setString(1, "luca");
        try {
            PswData hashed = PasswordManager.hashString("1234");
            pstmnt.setBytes(2, hashed.psw);
            pstmnt.setBytes(3, hashed.salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        pstmnt.executeUpdate();

        // query
        pstmnt = conn.prepareStatement("SELECT * FROM users WHERE Name=?");
        pstmnt.setString(1, "luca");
        ResultSet rs = pstmnt.executeQuery();

        rs.next();
        byte[] saltRocvered = rs.getBytes("Salt");
        byte[] hashRecovered = rs.getBytes("Password");

        if(PasswordManager.compareString("1234", saltRocvered, hashRecovered) == true){
            System.out.println("store and recover and hash OK!");
        }

        // print out query result
        while (rs.next()) {
            System.out.printf("%d\t%s\n", rs.getInt("ID"), rs.getString("Name"));
        }
    }
}
