package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

import java.util.concurrent.ThreadLocalRandom;

public class Professore extends Utente {

    private final TutorProf tutor;

    public Professore(TutorProf l) {
        this.tutor = l;
    }

    @Override
    protected void joinLab() throws InterruptedException {
        tutor.occupy();
    }

    @Override
    protected void useLab() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(200));
    }

    @Override
    protected void waitNextTurn() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(600));
    }

    @Override
    protected void leaveLab() {
        this.tutor.release();
    }

    @Override
    protected void printUserInLine() {
        System.out.print("[PROF " + Thread.currentThread().getName() + "]");
    }
}
