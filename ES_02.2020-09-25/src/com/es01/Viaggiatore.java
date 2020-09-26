package com.es01;

public class Viaggiatore implements Runnable {
    private final static int MAX_ATTESA = 1000;

    private final int id_traveler;

    /*
    la prima operazione consiste nello stampare “Viaggiatore
    {id}: sto acquistando un biglietto”, aspettare per un intervallo di tempo random tra 0 e 1000 ms e poi
    stampa “Viaggiatore {id}: ho acquistato il biglietto”.
     */

    public Viaggiatore(int z) {
        this.id_traveler = z;
    }

    public void run() {
        System.out.println("Viaggiatore {" + this.id_traveler +
                            "}: sto acquistando un biglietto");

        try {
            Thread.sleep((long) (Math.random()*MAX_ATTESA));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Viaggiatore {" + this.id_traveler +
                "}: ho acquistato il biglietto!");
    }

}
