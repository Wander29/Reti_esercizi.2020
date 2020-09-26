import java.util.concurrent.RejectedExecutionException;

/**
 * ESERCIZIO 1
 * Nella sala biglietteria di una stazione sono presenti 5 emettitrici automatiche dei biglietti. Nella sala non possono essere presenti più di 10 persone in attesa di usare le emettitrici.
 * Scrivere un programma che simula la situazione sopra descritta.
 *     • La sala della stazione viene modellata come una classe JAVA. Uno dopo l’altro arrivano 50 viaggiatori (simulare un intervallo di 50 ms con Thread.sleep).
 *     • ogni viaggiatore viene simulato da un task, la prima operazione consiste nello stampare “Viaggiatore {id}: sto acquistando un biglietto”, aspettare per un intervallo di tempo random tra 0 e 1000 ms e poi stampa “Viaggiatore {id}: ho acquistato il biglietto”.
 *     • I task vengono assegnati a un numero di thread pari al numero delle emettitrici
 *     • Il rispetto della capienza massima della sala viene garantita dalla coda gestita dal thread. I viaggiatori che non possono entrare in un certo istante perché la capienza massima è stata raggiunta abbandonano la stazione (il programma main stampa quindi “Traveler no.  {i}: sala esaurita”.
 *     • Suggerimento: usare un oggetto ThreadPoolExecutor in cui il numero di thread è pari al numero degli sportelli
 *
 * ESERCIZIO 2
 * Estendi il programma dell’Esercizio 1 gestendo la terminazione del threadpool.
 * Dopo l’arrivo dell’ultimo viaggiatore e l’invio del corrispondente task al threadpool, terminare il threadpool.
 */

/**
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class MainSalaBiglietteria {

    public static void main(String[] args) {
        final int n_viaggiatori = 50;
        final int max_attesa = 50;

        SalaBiglietteria sala = new SalaBiglietteria();

        for(int i=0; i<n_viaggiatori; i++){
            Viaggiatore v = new Viaggiatore(i);
            try {
                sala.serviViaggatore(v);
            }
            catch (RejectedExecutionException e){
                System.out.printf("Traveler no. %d: sala esaurita\n", v.getId());
            }

            try {
                Thread.sleep(max_attesa);
            }
            catch (InterruptedException e){
                System.out.println("Sleep interrotta");
            }
        }
        // ESERCIZIO 2
        sala.chiudiSala(5);
        // stampa il numero di viaggiatori serviti
        System.out.printf("Numero viaggiatori serviti: %d", sala.getViaggiatoriServiti());
    }

}
