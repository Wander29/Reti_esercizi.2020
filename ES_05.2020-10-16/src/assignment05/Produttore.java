package assignment05;

/**
 * @author      LUDOVICO VENTURI 578033
 * @version     1.0
 * @date        2020-10-22
 */

import java.io.*;

public class Produttore extends SlaveForDir implements Runnable {
    private final File startDir;

    public Produttore(File dir, ManagerQueue<String> c) {
        super(c);
        this.startDir = dir;
    }

    @Override
    public void run() {
        /*
            il produttore visita ricorsivamente la directory data ed, eventualmente tutte le sottodirectory
            e mette nella coda il nome di ogni directory individuata
         */
        this.discoverDirectory(this.startDir);

        // Protocollo di Chiusura connessione
        try {
            this.coda.closeQueue(this.CODICE_TERMINAZIONE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[PROD] termino");
    }

    /** DFS per la ricerca ricorsiva delle Directory e conseguente aggiunta in lista
     *
     * @param dir   cartella da analizzare
     */
    public void discoverDirectory(File dir) {
        if(dir.isDirectory()) {
           for(File f : dir.listFiles()) {
               if(f != null && f.exists() && f.isDirectory()) {
                   // per cercare di aumentare la concorrenza sulla coda
                   try {
                       Thread.sleep(20);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   discoverDirectory(f);
                   System.out.println("[PROD] scoperta DIR[" + f.getName() + "]");
                   this.coda.pushSync(f.getAbsolutePath());
               }
           }
        }
    }
}
