package com.es1;

/*
5. Estendere la classe Dropbox (overriding dei metodi take e put) usando il costrutto del monitor
    per gestire l’accesso di Consumer e Producer al buffer.
 */

public class DropboxSyncronized extends Dropbox {
    private boolean producer_active = false;
    private boolean consumer_active = false;

    public DropboxSyncronized() {
        super();
    }

    @Override
    public synchronized int take(boolean e) throws InterruptedException {
        String s = e ? "Pari" : "Dispari";

        while (!full || e == (num % 2 != 0)) { // num non è quello cercato
            System.out.println("Attendi per: " + s);
            wait();
        }

        System.out.println(s + " <-> " + num);
        full = false;
        notifyAll();

        return num;
    }

    @Override
    public synchronized void put(int n) throws InterruptedException {

        while(full) {
            try {
                wait();
            } catch (InterruptedException e1) {
                System.out.println("[PROD] Attesa sulla PUT");
            }
        }
        System.out.println("Producer ha inserito " + n);
        num = n;
        full = true;

        notifyAll();
    }
}
