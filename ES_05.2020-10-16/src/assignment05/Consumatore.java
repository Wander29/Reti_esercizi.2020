package assignment05;

/**
 * @author      LUDOVICO VENTURI 578033
 * @version     1.0
 * @date        2020-10-22
 */

import java.io.*;

public class Consumatore extends SlaveForDir implements Runnable {

    public Consumatore(ManagerQueue<String> c) { super(c); }

    @Override
    public void run() {
        /*
        i consumatori prelevano dalla coda i nomi delle directories e stampano il
            loro contenuto (nomi dei file)
         */
        String dir_read = null;
        File temp_dir = null;
        while(true) {
            if(Thread.interrupted()) {
                System.out.println("[CONS " + Thread.currentThread().getName() + "] sto per terminare");
                return;
            }

            try {
                dir_read = this.coda.pollSync();
            } catch (InterruptedException e) {
                System.out.println("[CONS " + Thread.currentThread().getName() + "] sto per terminare, interrotto");
                return;
            } catch (MyTerminationProtocolException e1) {
                // Gestione Terminazione (secondo il Protocollo da me definito)
                System.out.println("[CONS " + Thread.currentThread().getName() + "] sto per terminare");
                return;
            }

            if(dir_read == null) {
                System.out.println("[CONS " + Thread.currentThread().getName() + "] ERRORE: path nullo!");
            }

            temp_dir = new File(dir_read);
            if( !(temp_dir.exists() && temp_dir.isDirectory()) ) {
                System.out.println("[CONS " + Thread.currentThread().getName() + "] ERRORE: path letto: " + dir_read +
                        " MA non è una directory valida");
            }

            for(File f : temp_dir.listFiles()) {
                if(f != null && f.exists() && f.isFile()) {
                    System.out.println("[CONS " + Thread.currentThread().getName() + "] DIR[" + temp_dir.getName() +
                            "]: FILE " + f.getName());
                }
            }
        }
    }
}
