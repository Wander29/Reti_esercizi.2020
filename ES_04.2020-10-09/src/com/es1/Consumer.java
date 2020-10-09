package com.es1;

/*
2. Definire un task Consumer il cui metodo costruttore prende in ingresso un valore booleano (true per
    consumare valori pari e false per valori dispari) e il riferimento ad un’istanza di Dropbox.
    Nel metodo run invoca il metodo take sull’istanza di Dropbox.
 */

import java.util.concurrent.ThreadLocalRandom;

public class Consumer implements Runnable {
    private boolean valore_da_consumare;
    private final Dropbox risorsa;

    public Consumer(boolean flag, Dropbox db) {
        this.valore_da_consumare = flag;
        this.risorsa = db;
    }

    @Override
    public void run() {
        while(true) {
            try {
                risorsa.take(this.valore_da_consumare);
                if(Thread.interrupted()) {
                    System.out.println("[CONS] termino!");
                    return;
                }
                Thread.sleep(ThreadLocalRandom.current().nextInt(100,1000));
            } catch (InterruptedException e) {
                System.out.println("[CONS] Interrotto, termino!");
                return;
            }
        }
    }
}
