package assignment03;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/08
 * @versione	1.0
 */

import java.util.concurrent.ThreadLocalRandom;

public class Professore extends Utente {

    public Professore(Tutor l) {
        super(l);
    }

    @Override
    protected void joinLab() throws InterruptedException {
        tutor.book(this); // passa sè stesso per trovare il metodo giusto tramite overload e dynamic dispatch
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
        tutor.leave(this);
    }

    @Override
    protected void printUserInLine() {
        System.out.print("[PROF " + Thread.currentThread().getName() + "]");
    }
}
