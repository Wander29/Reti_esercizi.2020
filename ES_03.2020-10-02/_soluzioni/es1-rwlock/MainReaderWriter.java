import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Scrivere un programma in cui un contatore viene aggiornato da 20 scrittori e il suo valore letto e stampato da 20 lettori.
 *
 * 1- Creare una Classe Counter che offre i metodi increment() e get() per incrementare e recuperare il valore di un contatore. Vedi esempio di struttura di una classe Counter (non-thread safe) in allegato
 * 2- Definire un task Writer che implementa Runnable e nel metodo run invoca il metodo increment di un oggetto Counter
 * 3- Definire un task Reader che implementa Runnable e nel metodo run invoca il metodo get di un oggetto Counter e lo stampa
 * 4- Definire una classe contenente il metodo main. Nel main viene creata un’istanza di Counter. Vengono quindi creati 20 oggetti di tipo Writer e 20 oggetti di tipo Reader (a cui viene passato il riferimento all’oggetto counter nel costruttore). I task vengono quindi assegnati ad un threadpool (inviare al pool prima i writer e poi i reader) (suggerimento: usare un CachedThreadPool).
 * 5- Estendere la classe Counter fornita usando un oggetto di tipo ReentrantLock per garantire l’accesso in mutua esclusione alle sezioni critiche.
 * 6- Estendere la classe Counter usando al posto di ReentrantLock delle Read/Write Lock e confrontare l’intervallo di tempo richiesto dal threadpool per completare i task in questo caso col caso precedente (usare System.currentTimeMillis() per recuperare l’ora corrente, potete prendere un primo timestamp prima del ciclo di creazione dei task e il secondo timestamp dopo la terminazione del threadpool).
 * 7- (opzionale) Sostituire il threadpool di tipo CachedThreadPool con un FixedThreadPool, al variare del numero di thread (es. 1 ,2, 4) verificare l’intervallo di tempo richiesto dal threadpool per completare i task
 */

/**
 * @author Samuel Fabrizi
 * @version 1.1
 */

public class MainReaderWriter{

    public static void main(String[] args) {
        final int n_reader = 20;
        final int n_writer = 20;
        //Counter c = new ReentrantLockCounter();
        Counter c = new ReadWriteLockCounter();

        ExecutorService threadpool = Executors.newCachedThreadPool();
        //ExecutorService threadpool = Executors.newFixedThreadPool(1);
        //ExecutorService threadpool = Executors.newFixedThreadPool(2);
        //ExecutorService threadpool = Executors.newFixedThreadPool(4);

        long t1 = System.currentTimeMillis();
        for(int i=0; i<n_writer; i++){
            threadpool.execute(new Writer(c));
        }
        for(int i=0; i<n_reader; i++){
            threadpool.execute(new Reader(c));
        }
        threadpool.shutdown();
        try {
            while(!threadpool.isTerminated())
                threadpool.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Tempo impiegato: %d ms", System.currentTimeMillis() - t1);
    }

}
