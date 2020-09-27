package assignment02;

/**
 *  @author     LUDOVICO VENTURI 578033
 *  @date       2020/09/25
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class GestoreCode extends Thread {
    private final BlockingQueue bq;
    private final ThreadPoolExecutor tpe;
    private final ArrayBlockingQueue bq_tpe; // ricavato dal ThreadPoolExecutor

    private static final int ATTESA_ITERAZIONE = 300;

    /*
    * É un thread demone avente il compito di gestire le due code dell'ufficio postale
     */
    public GestoreCode(BlockingQueue bq, ThreadPoolExecutor tpe) {
        this.bq = bq;
        this.tpe = tpe;
        /* prendo la coda del threadPool
         * (avrei voluto usare l'ArrayBlockingQueue come in sola lettura dato che viene
         *   gestito dall'executor ma non saprei come fare)
         */
        this.bq_tpe = (ArrayBlockingQueue) this.tpe.getQueue();
    }

    @Override
    public void run() {
        while(true) {
            /* attende del tempo */
            try {
                Thread.sleep(ATTESA_ITERAZIONE);
            } catch (InterruptedException e) {
                System.out.println("[DAEMON] Sleep interrotta");
            }

            /* FINCHÉ la prima sala non è vuota E la seconda ha almeno un posto libero
                    fa entrare i clienti negli uffici dalla sala d'attesa*/
            while(this.bq_tpe.remainingCapacity() > 0 && !this.bq.isEmpty()) {

                Cliente cliente_in_cima = (Cliente) this.bq.remove();
                tpe.execute(cliente_in_cima);

                System.out.println("[CLIENTE " + cliente_in_cima.getIdCliente() + "] " +
                        "passa dalla sala d'attesa AGLI UFFICI..");
            }
        }
    }
}
