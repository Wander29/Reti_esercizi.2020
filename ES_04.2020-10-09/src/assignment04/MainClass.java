package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/10
 * @versione	1.1
 */

/***** ASS 4
	Risolvere il problema della simulazione del Laboratorio di informatica, assegnato
	nella lezione precedente, utilizzando il costrutto di Monitor
 */

/* ASS 3
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
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/********************************************************************************
 *  LOGICA DEL PROGRAMMA
 ********************************************************************************
		Vengono letti da tastiera il numero di professori/tesisti/studenti
		Vengono createi tutti gli utenti, un thread per ogni utente

		Il primo thread che viene eseguito entra nel laboratorio e da lì si applica la politica di controllo
		degli accessi.

		Le priorità sono garantite dalle condizioni di accesso al laboratorio nel codice del Tutor,
		dove viene data priorità di accesso ai professori, poi ai tesisti.


 		Ogni utente accede k volte al laboratorio, dopodichè termina
 		I thread vengono gestiti con un FixedThreadPool, di dimensione pari alla somma dei tipi di utenti
 */

/********************************************************************************
 *  STRUTTURA DELLE CLASSI
 ********************************************************************************
 *  	Professori, Tesisti e Studenti sono sottoclassi di Utente.
 *
 *  	Utente è una classe astratta che definisce il metodo run(), ha alcune
 *  		variabili e impone una certa "interfaccia"
 *
 *  	Laboratorio gestisce l'array delle postazioni, andando ad operare su
 *  	questa struttura dati.
 *
 *  	Tutor gestisce la concorrenza degli accessi al laboratorio, è un oggetto
 *  	i cui metodi sono tutti sezione critiche, usate da tutti i thread utente

 */

public class MainClass {
	
	public static void main(String[] args) {
		Laboratorio lab = new Laboratorio(); // laboratorio in sè
		Tutor tutor = new Tutor(lab);	// gestore del laboratorio

	/* generazione Utenti */
		int i;
		Scanner sc = new Scanner(System.in);

		/* professori */
		int n_prof = 0; /* conterrà il numero dei professori che vogliono accedere al laboratorio */
		do {
			System.out.println("Quanti Professori? in [0, 20]");
			try {
				n_prof = sc.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("ERRORE lettura input, riprovare");
			}
		} while(n_prof < 0 || n_prof > 20);

		/* tesisti */
		int n_tesi = 0;
		do {
			System.out.println("Quanti Tesisti? in [0, 50]");
			try {
				n_tesi = sc.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("ERRORE lettura input, riprovare");
			}
		} while(n_tesi < 0 || n_tesi > 50);

		/* studenti */
		int n_stud = 0;
		do {
			System.out.println("Quanti Studenti? in [0, 500]");
			try {
				n_stud = sc.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("ERRORE lettura input, riprovare");
			}
		} while(n_stud < 0 || n_stud > 500);

		if(n_prof + n_tesi + n_stud == 0)	return;

		/* attivazione Thread
	* 	realizzati tramite un threadPoolExecutor di dimensione fissa pari al numero di utenti
	* 	in modo da farli eseguire tutti contemporaneamente
	* */
		ThreadPoolExecutor tpe = (ThreadPoolExecutor)
					Executors.newFixedThreadPool(n_prof + n_tesi + n_stud);
		tpe.prestartAllCoreThreads();

		for(i = 0; i < n_prof; i++) { tpe.execute(new Professore(tutor)); }

		for(i = 0; i < n_tesi; i++) { tpe.execute(new Tesista(tutor)); }

		for(i = 0; i < n_stud; i++) { tpe.execute(new Studente(tutor)); }

		/* attesa terminazione Thread */
		tpe.shutdown();
		while(!tpe.isTerminated()) {
			try {
				tpe.awaitTermination(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				System.out.println("[MAIN] TPE.await interrotta");
				return;
			}
		}
	}
}








