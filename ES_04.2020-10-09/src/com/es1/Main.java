package com.es1;

/*
Scrivere un programma in cui alcuni thread generano e consumano numeri interi da una risorsa condivisa
(chiamata Dropbox) che ha capacità 1.

1. Nella classe Dropbox fornita in allegato il buffer di dimensione 1 è gestito tramite una variabile
intera num. La classe offre un metodo take per consumare il numero e svuotare il buffer e un metodo put
per inserire un nuovo valore se il buffer è vuoto.

2. Definire un task Consumer il cui metodo costruttore prende in ingresso un valore booleano (true per
consumare valori pari e false per valori dispari) e il riferimento ad un’istanza di Dropbox.
Nel metodo run invoca il metodo take sull’istanza di Dropbox.

3. Definire un task Producer il cui metodo costruttore prende in ingresso il riferimento
ad un’istanza di Dropbox. Nel metodo run genera un intero in modo random, nel range [0,100),
e invoca il metodo put sull’istanza di Dropbox.

4. Nel main viene creata un’istanza di Dropbox.
Vengono quindi creati 2 oggetti di tipo Consumer (uno true e uno false) e un oggetto di tipo
Producer, ciascuno eseguito da un thread distinto. Producono e pescano all'infinito.

5. Estendere la classe Dropbox (overriding dei metodi take e put) usando il costrutto del monitor
per gestire l’accesso di Consumer e Producer al buffer.

Notare la differenza nell’uso di notify vs notifyall.
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
	    Dropbox db = new DropboxSyncronized();

	    Consumer c1 = new Consumer(true, db);
	    Consumer c2 = new Consumer(false, db);
	    Producer p  = new Producer(db);

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        tpe.execute(c1);
        tpe.execute(c2);
        tpe.execute(p);
        tpe.prestartAllCoreThreads();

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            System.out.println("[MAIN] Sleep interrotta");
        }

        tpe.shutdownNow();
        while(!tpe.isTerminated()) {
            try {
                tpe.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("[TPE] Await interrotta");
            }
        }

        System.out.println("CORRECT EXITING");
    }
}
