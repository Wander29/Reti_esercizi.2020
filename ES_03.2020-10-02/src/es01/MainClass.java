package es01;

/*

Scrivere un programma in cui un contatore viene aggiornato da 20 scrittori e il suo valore letto e
stampato da 20 lettori.

    (opzionale) Sostituire il threadpool di tipo CachedThreadPool con un FixedThreadPool,
    al variare del numero di thread (es. 1 ,2, 4) verificare l’intervallo di tempo richiesto
    dal threadpool per completare i task
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainClass {

    public static void main(String[] args) {
        // Counter cnt = new CounterReentrant();
        Counter cnt = new CounterRW();


        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        // ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        int i;
        long start = System.currentTimeMillis();
        /* creazione pool */
        /*

        for(i = 0 ;i < 20; i++) {
            tpe.execute(new Reader(cnt, i));
            tpe.execute(new Writer(cnt, i));
        }
        */

        for(i = 0 ;i < 20; i++) {
            tpe.execute(new Writer(cnt, i));
        }
        for(i = 0 ;i < 20; i++) {
            tpe.execute(new Reader(cnt, i));
        }


        tpe.shutdown();
        while(!tpe.isTerminated()) {
            try {
                tpe.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrotta");
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("EXECUTION TIME: [" + (end-start) + " ms]\nCORRECT EXITING");

    }
}
