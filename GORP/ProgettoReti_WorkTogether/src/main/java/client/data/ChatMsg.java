package client.data;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ChatMsg {
    public String username;
    public String project;
    public Time sentTime;
    public String msg;

    public ChatMsg(String us, String pr, Time t, String m) {
        /*
        DateFormat df = new SimpleDateFormat("HH:mm");
        df.format(sentTime);
         */
        this.username = us;
        this.project = pr;
        this.sentTime = t;
        this.msg = m;
    }
}
