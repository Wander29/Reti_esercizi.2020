package server.logic;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import protocol.classes.Card;
import server.data.ServerProject;
import server.data.UserInfo;
import server.data.WorthData;
import utils.StringUtils;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SerializeHelper {
    private final static String MAIN_DIR    = ".dataBaseSurrogate" + File.separator;
    private final static String PROJ_DIR    = MAIN_DIR + "projects" + File.separator;
    private final static String USER_DIR    = MAIN_DIR + "users" + File.separator;
    private final static String USER_DB     = "users.json";
    private final static String projectMark = "#PROJECTINFO#";

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
                    #PROJECTINFO#
                    . card1
                    . card2
                    .
                    . cardn
                - project2
                    . ...
                - .....
             */
        File projectDir;
        String json;

        // serialize also static fields (default gson excludes TRANSIENT && STATIC, this way only the former)
        Gson gsonProjectInfo = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .create();

        for(Map.Entry<String, ServerProject> projectEntry: projectsMap.entrySet()) {
            ServerProject project = projectEntry.getValue();

            String projectPath = PROJ_DIR + project.getProjectName();
            projectDir = new File(projectPath);
            projectDir.mkdir();
            String projectDirName = projectPath + File.separator;

            // save project info
            json = gsonProjectInfo.toJson(project);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                    projectDirName + projectMark + project.getProjectName() + ".json"))
            ){
                writer.write(json);
                writer.flush();
            }
            catch (IOException e) { e.printStackTrace(); }

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
        json = gson.toJson(usersMap);

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

        Map<String, ServerProject> projectsMap  = new HashMap<>();
        /*
        - [projects] -> here
            - project1
                #PROJECTINFO#
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
                - [project1]  -> here
                    #PROJECTINFO#
                    . card1
                    . card2
                    .
                    . cardn
                - [project2] -> here
                    . ...
                - .....
             */

            String[] cards = projectDir.list();
            if(cards == null) {
                continue;
            }

            ServerProject serverProject             = null;
            Map<String,Card> toDoCards              = new HashMap<>();
            Map<String,Card> inProgressCards        = new HashMap<>();
            Map<String,Card> toBeRevisedCards       = new HashMap<>();
            Map<String,Card> doneCards              = new HashMap<>();

            for(String cardFileName : cards) // iterate into cards for one project
            {
                if (cardFileName.compareTo(".") == 0 || cardFileName.compareTo("..") == 0)
                    continue;

                String relativePathCardName = (projectDirName.endsWith(File.separator)) ?
                        projectDirName + cardFileName
                        : projectDirName + File.separator + cardFileName;

                File cardFile = new File(relativePathCardName);
                if(!cardFile.isFile())
                    continue;

                if(cardFileName.contains(projectMark)) // it's not a card but it's easier to use
                {
                    // it's a projectInfo
                    json = StringUtils.readFileAsString(relativePathCardName);
                    Type serverProjectType = new TypeToken<ServerProject>() {}.getType();

                    serverProject = gson.fromJson(json, serverProjectType);
                    continue;
                }

                 /*
                 - projects
                    - project1
                        #PROJECTINFO#
                        . [card1] -> here
                        . [card2] -> here
                        .
                        . [cardn] -> here
                    - project2
                        . ...
                    - .....
                 */
                json = StringUtils.readFileAsString(relativePathCardName);
                Type cardType = new TypeToken<Card>() {}.getType();
                Card card = gson.fromJson(json, cardType);

                // i have json of card
                switch(card.getStatus()) {

                    case TO_DO:
                        toDoCards.put(card.getCardName(), card);
                        break;

                    case IN_PROGRESS:
                        inProgressCards.put(card.getCardName(), card);
                        break;

                    case TO_BE_REVISED:
                        toBeRevisedCards.put(card.getCardName(), card);
                        break;

                    case DONE:
                        doneCards.put(card.getCardName(), card);
                        break;

                    default:
                        break;

                }

            }

            if(serverProject != null) {
                serverProject.setToDoCards(toDoCards);
                serverProject.setInProgressCards(inProgressCards);
                serverProject.setToBeRevisedCards(toBeRevisedCards);
                serverProject.setDoneCards(doneCards);

                projectsMap.put(serverProject.getProjectName(), serverProject);
            }
        }

        data.setProjects(projectsMap);
        return data;
    }
}
