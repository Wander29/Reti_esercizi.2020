package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/10
 * @versione	1.1
 */

import java.util.concurrent.ThreadLocalRandom;

public class Tesista extends Utente {
    private final int pc_tesi_personal;

    public Tesista(Tutor l) {
        super(l);
        pc_tesi_personal = this.tutor.identifyPcTesista();
    }

    @Override
    protected void joinLab() throws InterruptedException {
        tutor.book(this, pc_tesi_personal);
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
        tutor.leave(this, pc_tesi_personal);
    }

    @Override
    protected void printUserInLine() {
        System.out.print("[TESI " + Thread.currentThread().getName() + " PC [" + this.pc_tesi_personal
                + "] ]");
    }
}
