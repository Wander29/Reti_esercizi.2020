package com;

import java.net.InetAddress;
import java.util.*;

public class Project {
    String projectName;
    List<String> members;

    List<Card> toDoCards;
    List<Card> inProgressCards;
    List<Card> toBeRevisedCards;
    List<Card> doneCards;

    InetAddress chatMulticastIP;

    public Project(String s) {
        this.projectName = s;
    }
}
