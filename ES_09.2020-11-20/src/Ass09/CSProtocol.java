package Ass09;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/21
 * @version     1.0
 */

/**
 * classe astratta adibita a mantenere informazioni utili condivise fra Client e Server
 *
 * é possibile usare i suoi metodi statici per ottenere i valori delle variabili
 */

public abstract class CSProtocol {
    private static final int        TIMEOUT_JOIN = 20 * 1000; // ms

    public static int TIMEOUT_JOIN() { return TIMEOUT_JOIN; }
}
