package assignment03;

/*
Il laboratorio di Informatica del Polo Marzotto è utilizzato da tre tipi di utenti
studenti, tesisti e professori ed ogni utente deve fare una richiesta al tutor per accedere al laboratorio.
I computers del laboratorio sono numerati da 1 a 20. Le richieste di accesso sono diverse a seconda del
tipo dell'utente:

    - [professori] accedono in modo esclusivo a tutto il laboratorio, poichè hanno necessità di utilizzare
    tutti i computers per effettuare prove in rete.
    - [tesisti] richiedono l'uso esclusivo di un solo computer, identificato dall'indice i, poichè su quel
    computer è istallato un particolare software necessario per lo sviluppo della tesi.
    - [studenti] richiedono l'uso esclusivo di un qualsiasi computer.

I professori hanno priorità su tutti nell'accesso al laboratorio, i tesisti hanno priorità sugli studenti.
Nessuno può essere interrotto mentre sta usando un computer.

Scrivere un programma JAVA che simuli il comportamento degli utenti e del tutor.
Il programma riceve in ingresso il numero di studenti, tesisti e professori che utilizzano il
laboratorio ed attiva un thread per ogni utente. Ogni utente accede k volte al laboratorio,
con k generato casualmente. Simulare l'intervallo di tempo che intercorre tra un accesso ed il
successivo e l'intervallo di permanenza in laboratorio mediante il metodo sleep.

Il tutor deve coordinare gli accessi al laboratorio.
Il programma deve terminare quando tutti gli utenti hanno completato i loro accessi al laboratorio.

*/
    // attento a setPriority
    // un solo professore per volta, non più prof insieme

    // priorityQueue -> può essere un modo, daie, però


import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainClass {
	
	public static void main(String[] args) {
		Laboratorio lab = new Laboratorio();
		Tutor tutor = new Tutor(lab);

	/* generazione Utenti */
		int i;
		Scanner sc = new Scanner(System.in);

		/* professori */
		int n_prof = 0; /* conterrà il numero dei professori che vogliono accedere al laboratorio */
		do {
			System.out.println("Quanti Professori? in [1, 20]");
			try {
				n_prof = sc.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("ERRORE lettura input, riprovare");
			}
		} while(n_prof < 1 || n_prof > 20);

		/* tesisti */
		int n_tesi = 0;
		do {
			System.out.println("Quanti Tesisti? in [1, 50]");
			try {
				n_tesi = sc.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("ERRORE lettura input, riprovare");
			}
		} while(n_tesi < 1 || n_tesi > 50);

		/* studenti */
		int n_stud = 0;
		do {
			System.out.println("Quanti Studenti? in [1, 500]");
			try {
				n_stud = sc.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("ERRORE lettura input, riprovare");
			}
		} while(n_stud < 1 || n_stud > 500);


	/* attivazione Thread */
		ThreadPoolExecutor tpe = (ThreadPoolExecutor)
					Executors.newFixedThreadPool(n_prof + n_tesi + n_stud);

		for(i = 0; i < n_prof; i++) {
			tpe.execute(new Professore(tutor));
		}

		for(i = 0; i < n_tesi; i++) {
			tpe.execute(new Tesista(tutor));
		}

		for(i = 0; i < n_stud; i++) {
			tpe.execute(new Studente(tutor));
		}

		/* attesa terminazione Thread */
		tpe.shutdown();
		while(!tpe.isTerminated()) {
			try {
				tpe.awaitTermination(3, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				System.out.println("[MAIN] TPE.await interrotta");
				return;
			}
		}
		
	}

}








