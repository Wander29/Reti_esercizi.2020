package server.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import protocol.classes.Card;
import server.data.ServerProject;
import server.data.UserInfo;
import server.data.WorthData;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public abstract class SerializeHelper {
    /*
    1 dir per ogni progetto
    1 file per ogni card
        con dentro anche la lista movimenti
     */

    private final static String MAIN_DIR = "./.dataBaseSurrogate/";
    private final static String PROJ_DIR = MAIN_DIR + "projects/";
    private final static String USER_DIR = MAIN_DIR + "users/";
    private final static String USER_DB = "users.json";

    public static void saveData(WorthData data) throws IOException {

        Map<String, ServerProject> projectsMap = data.getProjects();
        Map<String, UserInfo> usersMap = data.getUsers();
        // create directory to act as database
        File dirMain = new File(MAIN_DIR);
        dirMain.mkdir(); // returns true iff it creates the dir

        /**
         * PROJECTS
         */
        File dirProj = new File(PROJ_DIR);
        dirProj.mkdir();

        for(Map.Entry<String, ServerProject> projectEntry: projectsMap.entrySet()) {

            ServerProject project = projectEntry.getValue();

            // creo una directory
            Map<String, Card> map = project.getToDoCards();

            for(Map.Entry<String, Card> entry : map.entrySet()) {
                // serialize card
                Card c = entry.getValue();
            }
        }

        /**
         * USERS
         */
        File dirUser = new File(USER_DIR);
        dirUser.mkdir();

        // Serialization
        Gson gson = new Gson();
        String json = gson.toJson(usersMap);

        BufferedWriter writer = new BufferedWriter(new
                FileWriter(USER_DIR + USER_DB));

        writer.write(json);
        writer.flush();

    }

    public static WorthData recoverData() throws IOException {

        // if database exists
        File usersDB = new File(USER_DIR + USER_DB);
        if (!usersDB.exists()) {
            return null;
        }

        Gson gson = new Gson();
        String json = readFileAsString(USER_DIR + USER_DB);

        WorthData data = new WorthData();
        Type usersMapType = new TypeToken<Map<String, UserInfo>>() {}.getType();
        data.setUsers(gson.fromJson(json, usersMapType));

        return data;
    }

    public static String readFileAsString(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}
