package assignment03;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Utente implements Runnable {
    protected final int MAX_EXEC = 10;
    protected Tutor tutor;

    public Utente(Tutor t) {
        this.tutor = t;
    }

    @Override
    public void run() {
        int i = 0;
        int k = ThreadLocalRandom.current().nextInt(MAX_EXEC) + 1;
        // int k = 1;
        while (i < k) {
            try {
                joinLab();
                printUserInLine();
                System.out.println(" sto per usare il lab!");

                useLab();

                printUserInLine();
                System.out.println(" sto per uscire");

                leaveLab();
            } catch (InterruptedException ex) {
                System.out.println("[UTENTE] Attesa interrotta");
                return;
            }
            i++;
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(200));
            } catch (InterruptedException e) {
                System.out.println("[UTENTE] Sleep di attesa per il prossimo accesso interrotta");
            }
        }
    }

    protected abstract void joinLab() throws InterruptedException;

    protected abstract void useLab() throws InterruptedException;

    protected abstract void leaveLab();

    protected abstract void printUserInLine();
}
