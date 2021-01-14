package client.data;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import javafx.collections.ObservableList;
import protocol.classes.ChatMsg;
import utils.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
    used: Apache DerbyDB

    this class will create multiple connections for one user (more object with same username)
    the two static variables will be shared
 */

public class DbHandler {

    private String dbUrl    = null;
    private Connection conn = null;
    private String username = null;
    // static variables => they need to be accessed in synchronized methods
    private static ObservableList<ChatMsgObservable> currentChatMsgList = null;
    private static String currentChat;

    public DbHandler(String username) throws SQLException {
        this.username               = username;
        this.currentChat            = "";

        dbUrl = "jdbc:derby:memory:client/" + this.username + "/db;create=true";

        // connect to DB
        conn = DriverManager.getConnection(dbUrl);
    }

    public synchronized void setObservableChatList(ObservableList<ChatMsgObservable> list) {
        if(this.currentChatMsgList == null)
            this.currentChatMsgList = list;
    }

    public void closeConnection() throws SQLException {
        this.conn.close();
    }

    public void createDB() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE Chats (" +
                        "ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "Username VARCHAR(128), " +
                        "Project VARCHAR(128)," +
                        "SentTime TIME," +
                        "Msg VARCHAR(2048))");

        System.out.println("[DB-HANDLER] tabella creata");

        // create index for efficiency
        stmt.executeUpdate("CREATE INDEX projIndex ON Chats (Project)");
    }

    public synchronized void saveChat(String username, String projectName, long timestamp, String msg)
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

            /*
            in order to update the chat as soon as a message arrives:
                - check if currentChat (shown on client screen) is the same
                    as received message's chat
             */
            if(this.currentChat.equals(projectName)){
                this.currentChatMsgList.add(new ChatMsgObservable(
                        username,
                        msg,
                        StringUtils.getTimeFormattedHHmm(time)
                ));
            }
    }

    // delete chat from DB when a project is closed in order to avoid problems with projects
        // having same name
    public synchronized void deleteChat(String projectName) throws SQLException {
        PreparedStatement pstmnt = conn.prepareStatement(
                "DELETE FROM Chats WHERE Project = ?");

        pstmnt.setString(1, projectName);
        pstmnt.executeUpdate();

        if(currentChat.equals(projectName)) {
            this.currentChat = "";
            this.currentChatMsgList.clear();
        }

        System.out.println("[DB-HANDLER] chat cancellata: " + projectName);
    }

    /*
    given a project, it returns all messages in that chat
     */
    public synchronized List<ChatMsg> readChat(String projectName) throws SQLException {
        PreparedStatement pstmnt = conn.prepareStatement(
                "SELECT * FROM Chats WHERE Project = ?");

        pstmnt.setString(1, projectName);

        ResultSet rs = pstmnt.executeQuery(); // rows in results
        List<ChatMsg> messages = new ArrayList<>();

        while (rs.next()) {
            ChatMsg msg = new ChatMsg(
                    rs.getString("Username"),
                    rs.getString("Project"),
                    rs.getTime("SentTime"),
                    rs.getString("Msg"));

            messages.add(msg);

        }

        // set current chat
        this.currentChat = projectName;
        this.currentChatMsgList.clear();
        addMsgToObservableList(messages);

        return messages;
    }

    // util
    private void addMsgToObservableList(List<ChatMsg> messages) {
        this.currentChatMsgList.clear();

        for(ChatMsg msg : messages) {
            this.currentChatMsgList.add(new ChatMsgObservable(
                    msg.username,
                    msg.msg,
                    msg.getTimeFormatted()
            ));
        }
    }
}
