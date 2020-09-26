import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ESERCIZIO 3
 * Scrivere un programma che calcola le potenze di un numero n (esempio n=2) da n2 a n50 e restituisce come risultato la somma delle potenze, ovvero:
 * Result = n^2 + n^3 + … + n^50
 *     • Creare una classe Power di tipo Callable che riceve come parametri di ingresso il numero n  e un intero (l’esponente), stampa “Esecuzione {n}^{esponente} in {idthread}”  e restituisce il risultato dell’elevamento a potenza (usare la funzione Math.pow() di Java https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#pow-double-double-)
 *     • Creare una classe che nel metodo public static void main(String args[]) crea un threadpool e gli passa i task Power.
 *     • I risultati restituiti dai task vengono recuperati e sommati e il risultato della somma viene stampato (usare una struttura dati, es. ArrayList per memorizzare gli oggetti di tipo Future restituiti dal threadpool in corrispondenza dell’invocazione del metodo submit).
 */

/**
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class MainPower {

    public static void main(String[] args) {
        final double base = 2;
        final int it = 50;
        double result = 0;

        // dichiarazione di un array con elementi di tipo Future
        ArrayList<Future<Double>> runningTasks= new ArrayList<Future<Double>>();
        // dichiarazione del threadpool
        ExecutorService threadPool = Executors.newFixedThreadPool(4);

        for (int i=2; i<it; i++){
            runningTasks.add(threadPool.submit(new Power(base, i)));
        }

        try{
            for (Future<Double> t: runningTasks ){
                result += t.get();
            }
            System.out.println("Result is "+ result);
        }
        catch (ExecutionException e){
            System.out.println("Some thread has failed");
        }
        catch (InterruptedException e) {
            System.out.println("Interrupted while waiting");
        }
        finally{
            threadPool.shutdown();
        }

    }
}