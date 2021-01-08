package protocol;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class CSProtocol {
    private final static boolean DEBUG              = true;
    private final static int RMI_SERVICE_PORT       = 10029;
    private final static int SERVER_PORT            = 11029;
    private final static String RMI_SERVICE_NAME    = "WorkTogether";
    private final static int BUF_SIZE_CLIENT        = 4096;
    private final static int BUF_SIZE_SERVER        = 4096;
    private final static Charset CHARSET            = StandardCharsets.UTF_8;


    public static boolean DEBUG()           { return DEBUG; }
    public static int RMI_SERVICE_PORT()    { return RMI_SERVICE_PORT; }
    public static int SERVER_PORT()         { return SERVER_PORT; }
    public static String RMI_SERVICE_NAME() { return RMI_SERVICE_NAME; }
    public static int BUF_SIZE_CLIENT()     { return BUF_SIZE_CLIENT; }
    public static int BUF_SIZE_SERVER()     { return BUF_SIZE_SERVER; }
    public static Charset CHARSET()         { return CHARSET; }
}