package assignment03;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/08
 * @versione	1.0
 */

import java.util.concurrent.ThreadLocalRandom;

public class Studente extends Utente {

    private int index;

    public Studente(Tutor l) {
        super(l);
    }

    @Override
    protected void joinLab() throws InterruptedException {
        index = tutor.book(this);
    }

    @Override
    protected void useLab() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(200));
    }

    @Override
    protected void waitNextTurn() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(200));
    }

    @Override
    protected void leaveLab() {
        tutor.leave(this, index);
    }

    @Override
    protected void printUserInLine() {
        System.out.print("[STUD " + Thread.currentThread().getName() + "]");
    }
}
