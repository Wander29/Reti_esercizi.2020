package assignment01;

/**
	@AUTHOR Ludovico Venturi 578033
	@DATE   2020/09/18
*/

/*
Scrivere un programma che attiva un thread T che effettua il calcolo approssimato di π.

Il programma principale riceve in input da linea di comando un parametro che indica il grado
di accuratezza (accuracy) per il calcolo di π ed il tempo massimo di attesa dopo cui il programma
principale interrompe il thread T.

Il thread T effettua un ciclo infinito per il calcolo di π usando la serie di Gregory-Leibniz
(π = 4/1 – 4/3 + 4/5 - 4/7 + 4/9 - 4/11 ...).

Il thread esce dal ciclo quando una delle due condizioni seguenti risulta verificata:
1) il thread è stato interrotto
2) la differenza tra il valore stimato di π ed il valore Math.PI (della libreria JAVA) è minore di accuracy
 */

import java.util.Scanner;

public class MainClass {
    public static void main(String arg[]) {

    /*
    * utilizzo una classe Runnable per gestire il task del calcolo di PI
    */
        WorkerRunnable workerTask = new WorkerRunnable();
    /* lettura degli input */
        Scanner scan = new Scanner(System.in);

        double acc_read;
        do {
            System.out.println("Set the ACCURACY (i.e.  |PI - computatedPI| ), must be IN (0,1)");
            acc_read = scan.nextDouble();
        } while(acc_read <= 0 || acc_read >= 1);
        workerTask.setAccuracy(acc_read);

        int max_time_ms;
        do {
            System.out.println("Set the MAX TIME you can wait [ms], must be > 0");
            max_time_ms = scan.nextInt();
        } while(max_time_ms <= 0);

    /* attivazione thread worker*/
        Thread workerThread =  new Thread(workerTask);
        workerThread.start();

    /*  attesa terminazione thread worker:
            ho un tempo max in cui aspetto che lui termini
    *       altrimenti lo interrompo, attendendone poi la terminazione con calma */
        try {
            workerThread.join(max_time_ms);
        } catch (InterruptedException e) {
            System.out.println("Main interrupted");
            workerThread.interrupt();
        }
        /* se è passato il timeout e non ha ancora terminato */
        if(workerThread.isAlive()) {
            workerThread.interrupt();
            try {
                workerThread.join(max_time_ms);
            } catch (InterruptedException e) {
                System.out.println("Main interrupted, NOT CORRECT EXITING");
            }
        }

        System.out.println("CORRECT EXITING");
        return;
    }
}
