import java.util.concurrent.*;


/**
 * UfficioPostaleMigliorato modella il comportamento di un ufficio postale. Con l'utilizzo di chiamate bloccanti è
 * stato possibile evitare l'attesa attiva
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class UfficioPostaleExecutor extends ThreadPoolExecutor {
    /**
     * oggetto utilizzato per la sincronizzazione
     */
    private Object lock;

    /**
     *
     * @param corePoolSize ThreadPoolExecutor#corePoolSize
     * @param maximumPoolSize ThreadPoolExecutor#maximumPoolSize
     * @param keepAliveTime ThreadPoolExecutor#keepAliveTime
     * @param q ThreadPoolExecutor#workQueue
     * @param lock oggetto utilizzato per la sincronizzazione
     */
    public UfficioPostaleExecutor (
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            BlockingQueue<Runnable> q,
            Object lock)
    {
        // @see https://bit.ly/3nxX0e3 (costruttore ThreadPoolExecutor)
        super(corePoolSize , maximumPoolSize , keepAliveTime, TimeUnit.SECONDS , q, new RejectedExecutionClienteHandler(lock));
        this.lock = lock;
    }

    @ Override
    protected void beforeExecute (Thread t , Runnable r) {
        synchronized (lock) {
            lock.notify();
        }
    }
}
