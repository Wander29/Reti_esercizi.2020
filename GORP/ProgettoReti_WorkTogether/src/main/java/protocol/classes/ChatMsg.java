package protocol.classes;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ChatMsg {
    public String username;
    public String project;
    public Time sentTime;
    public String msg;

    public ChatMsg(String us, String pr, Time t, String m) {
        this.username = us;
        this.project = pr;
        this.sentTime = t;
        this.msg = m;
    }

    // ex: HH:mm -> 22:34
    public String getTimeFormatted() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(sentTime);
    }
}