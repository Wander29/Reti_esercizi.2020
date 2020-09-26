import java.util.concurrent.*;

/**
 * SalaBiglietteria modella la sala biglietteria di una stazione
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class SalaBiglietteria {

    private final int nEmettitrici = 5;
    private final int capienzaMassima = 10;
    private final ExecutorService threadpoolEmettitrici;
    /**
     * numero dei viaggiatori serviti dalle emettitrici
     */
    private int viaggiatoriServiti;


    public SalaBiglietteria(){
        this.threadpoolEmettitrici = new ThreadPoolExecutor(nEmettitrici, nEmettitrici, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(capienzaMassima));
    }

    /**
     * se non è stata raggiunta la capienza massima serve un viaggiatore non appena si libera una emettitrice
     * @param v nuovo viaggiatore
     * @throws RejectedExecutionException se la sala ha già raggiunto la capienza massima
     */
    public void serviViaggatore(Viaggiatore v) throws RejectedExecutionException {
        try {
            this.threadpoolEmettitrici.execute(v);
        }
        catch (RejectedExecutionException e){
            throw new RejectedExecutionException();
        }
        viaggiatoriServiti++;
    }

    /**
     *
     * @link SalaBiglietteria#viaggiatoriServiti
     */
    public int getViaggiatoriServiti() {
        return viaggiatoriServiti;
    }

    /**
     *
     * @param timeout attesa massima per la chiusura delle emettitrici
     */
    public void chiudiSala(long timeout){
        this.threadpoolEmettitrici.shutdown();
        try {
            while(!this.threadpoolEmettitrici.isTerminated())
                this.threadpoolEmettitrici.awaitTermination(timeout, TimeUnit.SECONDS);
        }
        catch (InterruptedException e){
            System.out.println("awaitTermination interrotta");
        }
    }
}
