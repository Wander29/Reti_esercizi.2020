package assignment03;

import java.util.concurrent.ThreadLocalRandom;

public class Tesista extends Utente {

    public Tesista(Tutor l) {
        super(l);
    }

    @Override
    protected void joinLab() throws InterruptedException {
        tutor.book(this);
    }

    @Override
    protected void useLab() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(200));
    }

    @Override
    protected void leaveLab() {
        tutor.leave(this);
    }

    @Override
    protected void printUserInLine() {
        System.out.print("[TESI " + Thread.currentThread().getName() + "]");
    }
}
