package protocol;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class CSProtocol {
    private final static boolean DEBUG              = true;

    private final static int BUF_SIZE_CLIENT        = 4096;
    private final static int BUF_SIZE_SERVER        = 4096;
    private final static Charset CHARSET            = StandardCharsets.UTF_8;

    private final static String RMI_SERVICE_NAME    = "WorkTogether";
    private final static int RMI_SERVICE_PORT       = 10029;
    private final static int WORTH_TCP_PORT         = 11029;
    private final static int WORTH_DEFAULT_CHAT_SERVICE_PORT = 12029;

    public static boolean DEBUG()           { return DEBUG; }
    public static int RMI_SERVICE_PORT()    { return RMI_SERVICE_PORT; }
    public static int WORTH_TCP_PORT()    { return WORTH_TCP_PORT; }
    public static String RMI_SERVICE_NAME() { return RMI_SERVICE_NAME; }
    public static int BUF_SIZE_CLIENT()     { return BUF_SIZE_CLIENT; }
    public static int BUF_SIZE_SERVER()     { return BUF_SIZE_SERVER; }
    public static Charset CHARSET()         { return CHARSET; }
    public static int WORTH_DEFAULT_CHAT_SERVICE_PORT()
    { return WORTH_DEFAULT_CHAT_SERVICE_PORT; }

    public static void printRequest(String request) {
        System.out.println("request: " + request);
    }

    public static void printResponse(String response) {
        System.out.println("->: " + response + "\n");
    }

}