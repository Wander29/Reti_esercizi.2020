package com;

public abstract class ClientServerProtocol {
    private final static boolean DEBUG = true;
    private final static int RMI_SERVICE_PORT   = 10029;
    private final static int SERVER_PORT        = 11029;
    private final static String RMI_SERVICE_NAME = "WorkTogether";

    public static boolean DEBUG()           { return DEBUG; }
    public static int RMI_SERVICE_PORT()    { return RMI_SERVICE_PORT; }
    public static int SERVER_PORT()         { return SERVER_PORT; }
    public static String RMI_SERVICE_NAME() { return RMI_SERVICE_NAME; }
}