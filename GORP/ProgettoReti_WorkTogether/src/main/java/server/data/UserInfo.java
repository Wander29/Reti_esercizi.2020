package server.data;

import server.data.Project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserInfo implements Serializable {
    private String          username;
    private byte[]          psw;
    private List<String>    projects;   // projects of which he is a member

    public UserInfo(String us, byte[] psw) {
        this.username   = us;
        this.psw        = psw;
        this.projects   = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPsw() {
        return psw;
    }

    public List<String> getProjects() {
        return Collections.unmodifiableList(this.projects);
    }
}