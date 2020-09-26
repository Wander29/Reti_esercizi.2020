package es02;

/*
    Crea una classe DatePrinterThread che estenda java.lang.Thread. Aggiungi un metodo public
    static void main(String args[]) per creare e avviare una istanza di DatePrinterThread.
    Nel metodo public void run() crea un loop infinito con while(true).
    Nel corpo del loop devono essere eseguite le seguenti azioni: stampare data e ora correnti e
    nome del thread in esecuzione e stare in sleep per 2 secondi (2000 millisecondi).
    Nel metodo main(): crea un’istanza di DatePrinterThread and avviala usando start().
    Successivamente stampare di nuovo il nome del thread in esecuzione.
 */

import java.util.Calendar;
import java.util.Date;

public class DatePrinterThread extends java.lang.Thread {
    public DatePrinterThread() {}

    public void run() {
        while(true) {
            Calendar tempo = Calendar.getInstance();
            Date data = tempo.getTime();

            System.out.println("THREAD [" + Thread.currentThread().getName() + "] : Data e ora [ " +
                    data.toString() + "]");
            try {
                sleep(2000);
            } catch(InterruptedException e) {
                System.out.println("daie aoh");
            }
        }
    }

}
