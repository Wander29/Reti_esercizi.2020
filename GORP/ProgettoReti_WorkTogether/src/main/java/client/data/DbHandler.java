package client.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHandler {

    private static DbHandler instance;

    private static final String dbUrl = "jdbc:derby:memory:client/db;create=true";
    private static Connection conn = null;

    protected DbHandler() throws SQLException {
        // connect to DB
        conn = DriverManager.getConnection(dbUrl);

        // create
        Statement stmt = conn.createStatement();

        stmt.executeUpdate(
                "CREATE TABLE Chats (" +
                        "ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "Username VARCHAR(128), " +
                        "Project VARCHAR(128)," +
                        "SentTime TIME," +
                        "Msg VARCHAR(2048))");

        System.out.println("[DB-HANDLER] tabella creata");

        stmt.executeUpdate("CREATE INDEX projIndex ON Chats (Project)");
        System.out.println("[DB-HANDLER] indice creato");

    }

    public static synchronized DbHandler getInstance() throws SQLException {
        if(instance == null)
            instance = new DbHandler();

        return instance;
    }

    public static void saveChat(String username, String projectName, long timestamp, String msg)
            throws SQLException
    {
            PreparedStatement pstmnt = conn.prepareStatement("" +
                    "INSERT INTO Chats (Username, Project, SentTime, Msg) values (?, ?, ?, ?)");

            pstmnt.setString(1, username);
            pstmnt.setString(2, projectName);
            Time time = new Time(timestamp);
            pstmnt.setTime(3, time);
            pstmnt.setString(4, msg);


            pstmnt.executeUpdate();
        System.out.println("[DB-HANDLER] chat salvata");
    }

    /*
    given a project, it returns all messages in that chat
     */
    public static List<ChatMsg> readChat(String projectName) throws SQLException {
        PreparedStatement pstmnt = conn.prepareStatement(
                "SELECT Username, Project, SentTime, Msg FROM Chats WHERE Project=?");

        pstmnt.setString(1, projectName);

        ResultSet rs = pstmnt.executeQuery(); // rows in results
        System.out.println("[DB-HANDLER] query eseguita");
        List<ChatMsg> messages = new ArrayList<>();

        while (rs.next()) {
            ChatMsg msg = new ChatMsg(
                    rs.getString("Username"),
                    rs.getString("Project"),
                    rs.getTime("SentTime"),
                    rs.getString("Msg"));

            messages.add(msg);
        }

        return messages;
    }
}
