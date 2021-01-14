package server.data;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorthData {
    private Map<String, UserInfo> users;            // users login info
    private Map<String, ServerProject> projects;    // project data

    public WorthData() {
        this.users      = new HashMap<>();
        this.projects   = new HashMap<>();
    }

    public WorthData(WorthData recoveredData) {
        this.users      = recoveredData.getUsers();
        this.projects   = recoveredData.getProjects();

        System.out.println("- STATO SERVER RECUPERATO");
    }

    public WorthData(Map<String, UserInfo> users, Map<String, ServerProject> projects) {
        this.users      = users;
        this.projects   = projects;
    }

    public void setUsers(Map<String, UserInfo> users) {
        this.users = users;
    }

    public Map<String, UserInfo> getUsers() {
        return this.users;
    }

    public void setProjects(Map<String, ServerProject> projects) {
        this.projects = projects;
    }

    public Map<String, ServerProject> getProjects() {
        return this.projects;
    }
}
