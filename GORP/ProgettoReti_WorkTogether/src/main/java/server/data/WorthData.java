package server.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorthData {
    private Map<String, UserInfo> users;    // users login info
    private Map<String, ServerProject> projects;  // project data

    public WorthData() {
        this.users      = new ConcurrentHashMap<String, UserInfo>();
        this.projects   = new HashMap<>();
    }

    public WorthData(WorthData recoveredData) {
        this.users      = recoveredData.getUsers();
        this.projects   = recoveredData.getProjects();

        // System.out.println("- UTENTI RECUPERATI");
        // printUsers();
    }

    public void printUsers() {
        for(Map.Entry<String, UserInfo> user : this.getUsers().entrySet()) {
            System.out.println(user.getKey());
        }
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
