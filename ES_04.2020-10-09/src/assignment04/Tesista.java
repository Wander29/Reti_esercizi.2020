package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

import java.util.concurrent.ThreadLocalRandom;

public class Tesista extends Utente {
    private final int pc_tesi_personal;
    private final long matricola;
    private final TutorTesi tutor;

    public Tesista(TutorTesi l) {
        this.tutor = l;
        this.pc_tesi_personal = this.tutor.identifyPc();
        this.matricola = ThreadLocalRandom.current().nextLong(100000, 700000);
    }

    @Override
    protected void joinLab() throws InterruptedException {
        this.tutor.occupy(this.matricola, this.pc_tesi_personal);
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
        this.tutor.release(pc_tesi_personal);
    }

    @Override
    protected void printUserInLine() {
        System.out.print("[TESI " + Thread.currentThread().getName() + " PC [" + this.pc_tesi_personal
                + "] ]");
    }
}
