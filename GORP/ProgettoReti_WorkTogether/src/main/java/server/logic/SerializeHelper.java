package server.logic;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import protocol.classes.Card;
import server.data.ServerProject;
import server.data.UserInfo;
import server.data.WorthData;
import utils.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SerializeHelper {
    private final static String MAIN_DIR = ".dataBaseSurrogate" + File.separator;
    private final static String PROJ_DIR = MAIN_DIR + "projects" + File.separator;
    private final static String USER_DIR = MAIN_DIR + "users" + File.separator;
    private final static String USER_DB = "users.json";

    public static void saveData(WorthData data)
    {
        Map<String, ServerProject> projectsMap = data.getProjects();
        Map<String, UserInfo> usersMap = data.getUsers();

        // create directory to act as database
        File dirMain = new File(MAIN_DIR);
        dirMain.mkdir(); // returns true iff creates the dir

        /**
         * PROJECTS
         */
        File dirProjects = new File(PROJ_DIR);
        dirProjects.mkdir();
            /*
            - projects
                - project1
                    . card1
                    . card2
                    .
                    . cardn
                - project2
                    . ...
                - .....
             */
        File projectDir;

        for(Map.Entry<String, ServerProject> projectEntry: projectsMap.entrySet()) {
            ServerProject project = projectEntry.getValue();

            String projectDirName = PROJ_DIR + project.getProjectName();
            projectDir = new File(projectDirName);
            projectDir.mkdir();

            saveListCard(project.getToDoCards(), projectDirName);
            saveListCard(project.getInProgressCards(), projectDirName);
            saveListCard(project.getToBeRevisedCards(), projectDirName);
            saveListCard(project.getDoneCards(), projectDirName);
        }

        /**
         * USERS
         */
        File dirUser = new File(USER_DIR);
        dirUser.mkdir();

        // Serialization
        Gson gson = new Gson();
        String json = gson.toJson(usersMap);

        try (BufferedWriter writer =
                    new BufferedWriter(new FileWriter(USER_DIR + USER_DB))
        ){
            writer.write(json);
            writer.flush();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private static void saveListCard(Map<String, Card> map, String projectDir) {

        Gson gson = new Gson();
        for(Map.Entry<String, Card> cardEntry : map.entrySet())
        {
            Card c = cardEntry.getValue();
            String json = gson.toJson(c);

            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(projectDir + c.getCardName() + ".json"))
            ){
                writer.write(json);
                writer.flush();
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static WorthData recoverData() throws IOException {

        // if database exists
        File usersDB = new File(USER_DIR + USER_DB);
        if (!usersDB.exists()) {
            return null;
        }

        // get users
        String json = StringUtils.readFileAsString(USER_DIR + USER_DB);
        WorthData data = new WorthData();

        Gson gson = new Gson();
        Type usersMapType = new TypeToken<Map<String, UserInfo>>() {}.getType();
        data.setUsers(gson.fromJson(json, usersMapType));

        // get projects
        File dirProjects = new File(PROJ_DIR);
        if(!dirProjects.exists())
            return data;

         /*
            - [projects] here
                - project1
                    . card1
                    . card2
                    .
                    . cardn
                - project2
                    . ...
                - .....
             */

        String[] projectDirs = dirProjects.list();
        if(projectDirs == null)
            return data;

        for(String s : projectDirs) // iterate into projects
        {
            if (s.compareTo(".") == 0 || s.compareTo("..") == 0)
                continue;

            String projectDirName = PROJ_DIR + s;
            File projectDir = new File(projectDirName);
            if(!projectDir.isDirectory())
                continue;

             /*
            - projects
                - [project1]  here
                    . card1
                    . card2
                    .
                    . cardn
                - [project2] here
                    . ...
                - .....
             */

            String[] cards = projectDir.list();
            if(cards == null) {
                continue;
            }

            for(String cardFileName : cards) // iterate into cards for one project
            {
                if (s.compareTo(".") == 0 || s.compareTo("..") == 0)
                    continue;

                String relativePathCardName = (projectDirName.endsWith(File.separator)) ?
                        projectDirName + cardFileName + ".json"
                        : projectDirName + File.separator + cardFileName + ".json";

                File cardFile = new File(relativePathCardName);
                if(!projectDir.isFile())
                    continue;

                 /*
                 - projects
                    - project1
                        . [card1] here
                        . [card2] here
                        .
                        . [cardn] here
                    - project2
                        . ...
                    - .....
                 */
                json = StringUtils.readFileAsString(relativePathCardName);
                Type cardType = new TypeToken<Card>() {}.getType();
                Card card = gson.fromJson(json, cardType);

                System.out.println(json);
            }
        }

        //gson.fromJson(json, usersMapType);
        //data.setProjects();
        return data;
    }
}
