package assignment02;

/**
 *  @author     LUDOVICO VENTURI 578033
 *  @date       2020/09/25
 *  @version    1.1
 */

/*********************************************
 * LOGICA DEL PROGRAMMA
 * *******************************************
 *  Vengono creati C clienti, e dovranno essere tutti gestiti
 *  I primi 4 + k clienti vengono gestiti immediatamente dal ThreadPoolExecutor
 *  I restanti vengono messi in attesa in una coda che rappresenta la sala d'attesa
 *
 *  Il risveglio di questi clienti in attesa viene realizzato da un thread DEMONE che
 *  ogni 300ms si sveglia e controlla che:
 *      - la coda dell'executor non sia vuota   E
 *      - che ci siano clienti in sala d'attesa
 *  In questo modo i clienti vengono eseguiti in ordine di arrivo
 *
 *  VERSIONE 1.2
 *  ho simulato un flusso continuo di clienti, usando la classe CreaFlusso
 *  Per far eseguire il codice dell'assignment, decommentare la parte "1.0" e commentare la parte "1.1"
 */

/*
Simulare il flusso di clienti in un ufficio postale che ha 4 sportelli. Nell'ufficio esiste:

    un'ampia sala d'attesa in cui ogni persona può entrare liberamente.
    Quando entra, ogni
    persona prende il numero dalla numeratrice e aspetta il proprio turno in questa sala.
    una seconda sala, meno ampia, posta davanti agli sportelli, in cui possono essere presenti
    al massimo k persone (oltre alle persone servite agli sportelli)
    Una persona si mette quindi prima in coda nella prima sala, poi passa nella seconda sala.
    Ogni persona impiega un tempo differente per la propria operazione allo sportello. Una volta
    terminata l'operazione, la persona esce dall'ufficio

Scrivere un programma in cui:

    l'ufficio viene modellato come una classe JAVA, in cui viene attivato un ThreadPool di
    dimensione uguale al numero degli sportelli

    la coda delle persone presenti nella sala d'attesa è gestita esplicitamente dal programma
    la seconda coda (davanti agli sportelli) è quella gestita implicitamente dal ThreadPool

    ogni persona viene modellata come un task, un task che deve essere assegnato ad uno dei
    thread associati agli sportelli
    si preveda di far entrare tutte le persone nell'ufficio postale, all'inizio del programma
 */

import java.util.Scanner;

public class MainClass {

    public static void main(String[] args) {
        final int TIMEOUT_CHIUSURA = 1000;

        Scanner sc = new Scanner(System.in);
        int k;
        do {
            System.out.println("Inserisci la dimensione della coda della 2° sala (K, IN [1, 100] )");
            k = sc.nextInt();
        } while (k < 1 || k > 100);

        Ufficio uff = new Ufficio(k);
        /*
        cliente viene creato, va in attesa nella prima sala
            SE la seconda sala ha spazio viene servito
            altrimenti aspetta in una coda secondaria
         */
        /* ********************************************
         * VERSIONE 1.0
         * ********************************************/
        /*
        int c;
        do {
            System.out.println("Inserisci il numero di clienti (C, IN [1, 9999] )");
            c = sc.nextInt();
        } while (c < 1 || c > 9999);

        for(int i = 0; i < c; i++) {
        // l'ingresso nella prima sala viene 'simulato' ed eventualmente trascurato
            uff.serviCliente(new Cliente(i));
        }
        */

        /* ********************************************
         * VERSIONE 1.1
         * ********************************************/
        CreaFlusso cl = new CreaFlusso(uff);
        cl.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Main interrotto, SLEEP");
        }
        while(cl.isAlive()) {
            cl.interrupt();
            try {
                cl.join(500);
            } catch (InterruptedException e) {
                System.out.println("Main interrotto, JOIN");
            }
        }



        uff.chiudiUffici(TIMEOUT_CHIUSURA);
        System.out.println("TERMINAZIONE CORRETTA");
    }
}
