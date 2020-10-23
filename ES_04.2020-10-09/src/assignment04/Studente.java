package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

import java.util.concurrent.ThreadLocalRandom;

public class Studente extends Utente {

    private final TutorStud tutor;
    private int pc_taken;

    public Studente(TutorStud l) {
        this.tutor = l;
    }

    @Override
    protected void joinLab() throws InterruptedException {
        this.pc_taken = this.tutor.occupy();
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
        this.tutor.release(this.pc_taken);
    }

    @Override
    protected void printUserInLine() {
        System.out.print("[STUD " + Thread.currentThread().getName() + "]");
    }
}
