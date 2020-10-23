package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

import java.util.concurrent.ThreadLocalRandom;

/**
 * UTENTE: Classe astratta che implementa RUN ed impone una certa interfaccia
 */

public abstract class Utente implements Runnable {
    protected final int MAX_EXEC = 10; // numero massimo accessi al lab da parte di un utente

    private static final boolean DEBUG = true;

    @Override
    public void run() {
        int i = 0;
        int k = ThreadLocalRandom.current().nextInt(2, MAX_EXEC);
        //int k = 1;

        while (i < k) {
            try {
                // prova ad accedere al laboratorio
                joinLab();
                if(DEBUG) {
                    printUserInLine();
                    System.out.println(" sto per usare il lab!");
                    System.out.flush();
                }

                // accede al laboratorio
                useLab();

                // uscita dal laboratorio
                if(DEBUG) {
                    printUserInLine();
                    System.out.println(" sto per uscire");
                    System.out.flush();
                }
                leaveLab();

                // attesa prossimo turno
                i++;
                waitNextTurn();
            } catch (InterruptedException ex) {
                System.out.println("[UTENTE " + Thread.currentThread().getName() + "] Attesa interrotta");
                return;
            }
        }
        printUserInLine();
        System.out.println(" sto per Terminare!");
        System.out.flush();
    }

    protected abstract void joinLab() throws InterruptedException;

    protected abstract void useLab() throws InterruptedException;

    protected abstract void waitNextTurn() throws InterruptedException;

    protected abstract void leaveLab();

    protected abstract void printUserInLine();
}
