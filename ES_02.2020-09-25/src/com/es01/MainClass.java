package com.es01;

/* ES 1
Nella sala biglieteria di una stazione sono presenti 5 emettitrici automatiche dei biglietti.
Nella sala non possono essere presenti più di 10 persone in attesa di usare le emettitrici.

Scrivere un programma che simula la situazione sopra descritta.
    La sala della stazione viene modellata come una classe JAVA.

    Uno dopo l’altro arrivano 50 viaggiatori (simulare un intervallo di 50 ms con Thread.sleep).
    ogni viaggiatore viene simulato da un task, la prima operazione consiste nello stampare “Viaggiatore
    {id}: sto acquistando un biglietto”, aspettare per un intervallo di tempo random tra 0 e 1000 ms e poi
    stampa “Viaggiatore {id}: ho acquistato il biglietto”.
    I task vengono assegnati a un numero di thread pari al numero delle emettitrici
    Il rispetto della capienza massima della sala viene garantita dalla coda gestita dal threadpool.
    I viaggiatori che non possono entrare in un certo istante perché la capienza massima è stata
    raggiunta abbandonano la stazione (il programma main stampa quindi “Traveler no.  {i}: sala esaurita”.

    -> alcuni task saranno rifiutati!

    Suggerimento: usare un oggetto ThreadPoolExecutor in cui il numero di thread è pari al numero
    degli sportelli
 */

/* ES 2
Estendi il programma dell’Esercizio 1 gestendo la terminazione del threadpool.

Dopo l’arrivo dell’ultimo viaggiatore e l’invio del corrispondente task al threadpool, terminare il threadpool.
 */

import java.util.concurrent.*;

public class MainClass {

    public static void main(String[] args) {

        final int NUM_SPORTELLI     = 5;
        final int NUM_VIAGGIATORI   = 50;
        final int DIM_MAX_CODA      = 10;

        Sportelli sp = new Sportelli();

        /* viaggiatori */
        for(int i = 0; i < NUM_VIAGGIATORI; i++) {
            Viaggiatore traveler_tmp = new Viaggiatore(i);

            try {
                sp.executeTask(traveler_tmp);
            } catch (RejectedExecutionException e) {
                System.out.println("Viaggiatore {" + i + "}: sala esaurita, so sad :(");
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        sp.gracefulShutdown(200);

        System.out.println("CORRECT TERMINATION");
    }
}
