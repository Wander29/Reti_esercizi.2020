package com.es01;

/*
Non creerai un Thread poichè la JVM crea sempre un main Thread

    Crea la classe DatePrinter con un metodo public static void main(String args[]) a cui
    aggiungerai il codice per i passi seguenti.

    Crea un loop infinito con while(true).
    Nel corpo del loop devono essere eseguite le seguenti azioni: stampare data e ora correnti
    e nome del thread in esecuzione (suggerimento: usa Thread.currentThread()) e successivamente
    stare in sleep per 2 secondi. Suggerimento: usa
    java.util.Calendar https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html
    per recuperare data e ora correnti.

Al termine dell’esercizio provare ad aggiungere dopo il ciclo while l’operazione di
stampa del nome del thread in esecuzione.

 */

import java.util.Calendar;
import java.util.Date;

import static java.lang.Thread.sleep;

public class DatePrinter {

    public static void main(String[] args) {

        while(true) {
            // Calendar tempo = Calendar.getInstance();
            // Date data = tempo.getTime();

            System.out.println("THREAD [" + Thread.currentThread().getName() + "] : Data e ora [ " +
                    Calendar.getInstance().getTime() + "]");
            try {
                sleep(2000);
            } catch(InterruptedException e) {
                // si può controllare il flag di interruzione qua
                // la gestione del flag dipende da quel che si deve fare, può essere inserito
                // se uno invoca la INTERRUPT e strutturo la terminazione, devo gestirla

                System.out.println("daie aoh");
            }
        }
        // System.out.println("THREAD [" + Thread.currentThread() + "]");
    }
}
