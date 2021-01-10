package protocol.classes;

import java.io.Serializable;

public class ListProjectEntry implements Serializable {
    public String project;
    public String ip;
    public int port;

    public ListProjectEntry(String project, String ip, int port){
        this.project = project;
        this.ip = ip;
        this.port = port;
    }
}
