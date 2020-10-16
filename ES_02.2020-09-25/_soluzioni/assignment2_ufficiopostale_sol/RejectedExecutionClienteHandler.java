import java.util.concurrent.*;

/**
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class RejectedExecutionClienteHandler implements RejectedExecutionHandler {
    final private Object lock;

    public RejectedExecutionClienteHandler (Object lock) {
        this.lock = lock;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor ) {
        // System.out.printf("Cliente numero %d: posti seconda sala esauriti \n", ((Cliente) r).getNumero());
        synchronized (lock) {
            try {
                //mi metto in attesa sulla lock
                //un thread della pool mi notifichera ' prima che inizi a servire un cliente
                lock.wait();
                // dopo che sono stato notificato posso aggiungere il client
                // alla sala piccola
                executor.execute(r);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}