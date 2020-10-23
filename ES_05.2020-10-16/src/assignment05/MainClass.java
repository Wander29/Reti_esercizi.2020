package assignment05;

/**
 * @author      LUDOVICO VENTURI 578033
 * @version     1.0
 * @date        2020-10-22
 */

/*
Si scriva un programma JAVA che

    riceve in input un filepath che individua una directory D
    stampa le informazioni del contenuto di quella directory e, ricorsivamente,
            di tutti i file contenuti nelle sottodirectory di D

iI programma deve essere strutturato come segue:

     attiva un thread produttore ed un insieme di k thread consumatori

     il produttore comunica con i consumatori mediante una coda

    il produttore visita ricorsivamente la directory data ed, eventualmente tutte le sottodirectory
        e mette nella coda il nome di ogni directory individuata

    i consumatori prelevano dalla coda i nomi delle directories e stampano il loro contenuto (nomi dei file)

    la coda deve essere realizzata con una LinkedList.
    Ricordiamo che una Linked List non è una struttura thread-safe. Dalle API JAVA
    “Note that the implementation is not synchronized.
        If multiple threads access a linked list concurrently, and at least one of the threads modifies
        the list structurally, it must be synchronized externally”
 */

import java.io.*;

public class MainClass {
    protected static final String CODICE_TERMINAZIONE = "029.TERMINATE";

    static void printUsage() {
        System.out.println("USAGE: <path-to-dir> <k=num_consumatori>");
    }

    public static void main(String[] args) {
        if(args.length != 2) { printUsage(); return; }

        File startDir = new File(args[0]);
        int k = 0;

        try {
            k = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("numero consumatori scorretto: deve essere compreso IN [1, 10]: " + args[0]);
            printUsage();
            return;
        }
        if(k > 50 || k < 1) {
            System.out.println("numero consumatori scorretto: deve essere compreso IN [1, 50]: " + args[0]);
            printUsage();
            return;
        }

        if(!startDir.exists() || !startDir.isDirectory()) {
            System.out.println("Path scorretto: non ho trovato una Directory in questo indirizzo: " + args[0]);
            printUsage();
            return;
        }

        ManagerQueue<String> coda_directories = new ManagerQueue<>(new String(CODICE_TERMINAZIONE));
        // PRODUTTORE -> visiterà tutte le Dir
        Thread prod = new Thread(new Produttore(startDir, coda_directories));
        prod.start();

        // CONSUMATORI -> apriranno le directory salvate in coda (concorrentemente)
        //  e stamperanno i file contenuti nella dir
        Thread[] pool = new Thread[k];
        for(int i = 0; i < k; i++) {
            pool[i] = new Thread(new Consumatore(coda_directories));
            pool[i].start();
        }

        // attesa TERMINAZIONE
        try {
            prod.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < k; i++) {
            try {
                pool[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("TERMINAZIONE CORRETTA!");
    }
}
