package assignment02;

/**
 *  @author     LUDOVICO VENTURI 578033
 *  @date       2020/09/25
 */

import java.util.concurrent.*;

public class Ufficio {
    private final int n_sportelli = 4;

    private final ThreadPoolExecutor sportelli;
    private LinkedBlockingQueue coda;
    private final ArrayBlockingQueue abq;

    private final GestoreCode gc;

    public Ufficio(int dim_coda) {
        this.abq = new ArrayBlockingQueue<Runnable>(dim_coda);
        this.sportelli = new ThreadPoolExecutor(n_sportelli, n_sportelli, 0L,
                TimeUnit.MILLISECONDS, abq);
        this.coda = new LinkedBlockingQueue(); // sala d'attesa illimitata

        /* attivazione thread Demone che gestirà le code
        * (avrei voluto passare l'ArrayBlockingQueue come in sola lettura dato che viene
        *   gestito dall'executor ma non saprei come fare) */
        this.gc = new GestoreCode(this.coda, this.abq, this.sportelli);
        this.gc.setDaemon(true);
        this.gc.start();
    }

    /**
     * Se la sala uffici ha spazio il cliente viene gestito dall'Executor, altrimenti
     *  va in sala d'attesa: sarà poi il  Gestore delle code a mandarlo in esecuzione
     * @param task      compito da eseguire, in questo caso servire il cliente
     */
    public void serviCliente(Cliente task) {
        if(!coda.isEmpty()) {
            coda.add(task);
            System.out.println("[CLIENTE " + task.getIdCliente() + "] in sala d'attesa");
        }else {
            try {
                this.sportelli.execute(task);
            } catch (RejectedExecutionException e) {
                // la sala uffici è piena, metto il cliente in coda in sala d'attesa
                coda.add(task);
                System.out.println("[CLIENTE " + task.getIdCliente() + "] in sala d'attesa");
            }
        }
    }

    /**
     * Chiude gli uffici in maniera 'soft', fa terminare TUTTI i clienti, anche in sala d'attesa
     * @param timeout       intervallo di attesa
     */
    public void chiudiUffici(long timeout) {
        /* posso terminare il programma solo SE tutti i clienti saranno serviti, compresi quelli in
        *   sala d'attesa */
        while(!this.coda.isEmpty()) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                System.out.println("[UFFICIO] Sleep interrotta");
            }
        }

        this.sportelli.shutdown();
        while(!this.sportelli.isTerminated()) {
            try {
                this.sportelli.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.out.println("[UFFICIO] Sleep interrotta");
            }
        }
    }
}
