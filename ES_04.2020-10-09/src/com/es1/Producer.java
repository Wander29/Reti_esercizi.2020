package com.es1;

import java.util.concurrent.ThreadLocalRandom;
/*
3. Definire un task Producer il cui metodo costruttore prende in ingresso il riferimento
    ad un’istanza di Dropbox. Nel metodo run genera un intero in modo random, nel range [0,100),
    e invoca il metodo put sull’istanza di Dropbox.
 */
public class Producer implements Runnable {
    private final Dropbox risorsa;

    public Producer(Dropbox db) {
        this.risorsa = db;
    }

    @Override
    public void run() {
        while(true) {
            try {
                risorsa.put(ThreadLocalRandom.current().nextInt(100));

                if(Thread.interrupted()) {
                    System.out.println("[PROD] termino!");
                    return;
                }
                Thread.sleep(ThreadLocalRandom.current().nextInt(100,1000));
            } catch (InterruptedException e_ext) {
                System.out.println("[PROD] Interrotto, termino!");
                return;
            }
        }
    }
}
