package Ass09;

public abstract class ClientErrors {
    private static final String ERR_PORT        = "ERR --port -p <server port>";
    private static final String ERR_SERV_NAME   = "ERR --name -n <server name>";

    public static String ERR_PORT()         { return ERR_PORT; }
    public static String ERR_SERV_NAME()    { return ERR_SERV_NAME; }
}
