package server.data;

import utils.psw.PswData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 01L;

    private String          username;
    private PswData         psw;
   // private List<String>    projects;   // projects of which he is a member

    public UserInfo(String us, PswData psw) {
        this.username   = us;
        this.psw        = psw;
       // this.projects   = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public PswData getPsw() {
        return psw;
    }
}