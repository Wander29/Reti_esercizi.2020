package assignment03_v2;

import java.util.concurrent.PriorityBlockingQueue;

/*
    MANAGER -> handles concurrency of the shared resource: Laboratory,
        mainly through a PriorityBlockingQueue
 */

public class Manager extends AbstractLab implements Runnable {

    private PriorityBlockingQueue<Utente> queue;
    private final Laboratorio lab;

    public Manager(Laboratorio l) {
        queue = new PriorityBlockingQueue<>(this.LAB_SIZE, new ComparatoreUtenti());
        lab = l;
    }

    public void run() {

        Utente curr_user;
        while(Thread.interrupted()) {
            /* wait until someone wants to enter the laboratory */
            try {
                curr_user = queue.take();
            } catch (InterruptedException e) {
                System.out.println("[MANAGER] interrotto");
            }

            /* book a computer in lab */
            lab.book(this);

            /*  */
            curr_user.

        }



    }

}
