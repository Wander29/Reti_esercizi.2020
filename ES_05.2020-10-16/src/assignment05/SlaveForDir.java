package assignment05;

/**
 * @author      LUDOVICO VENTURI 578033
 * @version     1.0
 * @date        2020-10-22
 */

/**
 * classe astratta utile per il costruttore e per il Protocollo di chiusura
 */
public abstract class SlaveForDir {
    protected static final String CODICE_TERMINAZIONE = "029.TERMINATE";

    protected final ManagerQueue<String> coda;

    public SlaveForDir(ManagerQueue<String> c) {
        this.coda = c;
    }
}
