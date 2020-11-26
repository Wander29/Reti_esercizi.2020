package Ass08;

/**
 * classe astratta adibita a mantenere informazioni utili per la comunicazione Client-Server
 *
 * Non instanziabile, ma è possibile usare i suoi metodi statici
 */

public abstract class CSProtocol {
    private static final int TIMEOUT_JOIN_SERVER = 20 * 1000; // ms
    private final static String EXIT_STRING = "EXIT_029";

    public static String EXIT_STRING() {
        return EXIT_STRING;
    }

    public static int TIMEOUT_JOIN_SERVER() {
        return TIMEOUT_JOIN_SERVER;
    }
}
