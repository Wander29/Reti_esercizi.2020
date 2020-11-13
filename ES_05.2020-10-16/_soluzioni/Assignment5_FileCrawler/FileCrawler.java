import java.io.File;
import java.util.*;

/**
 * Si scriva un programma JAVA che
 * - riceve in input un filepath che individua una directory D
 * - stampa le informazioni del contenuto di quella directory e, ricorsivamente, di tutti i file contenuti nelle sottodirectory di D
 * Il programma deve essere strutturato come segue:
 * - attiva un thread produttore ed un insieme di k thread consumatori
 * - il produttore comunica con i consumatori mediante una coda
 * - il produttore visita ricorsivamente la directory data ed, eventualmente tutte le sottodirectory e mette nella coda il nome di ogni directory individuata
 * - i consumatori prelevano dalla coda i nomi delle directories e stampano il loro contenuto  (nomi dei file)
 * - la coda deve essere realizzata con una LinkedList. Ricordiamo che una Linked List non è una struttura thread-safe. Dalle API JAVA “Note that the implementation is not synchronized. If multiple threads access a linked list concurrently, and at least one of the threads modifies the list structurally, it must be synchronized externally”
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class FileCrawler {

	/**
	 * numero dei consumatori
	 */
	public static final int K = 2;

	public static void main(String[] args) {
		WorkQueue directories = new WorkQueue();
		String baseDir = "testDir";

		if (args.length > 0) {
			baseDir = args[0];
			File startDirectory = new File(baseDir);

			if(!startDirectory.exists()) {
				System.out.println("Il file iniziale non esiste");
				System.exit(-1);
			}

			if(!startDirectory.isDirectory()) {
				System.out.println("Il file iniziale non è una directory");
				System.exit(-1);
			}
		}


		// crea ed avvia il produttore
		Thread p = new Thread(new Producer(baseDir, directories));
		p.start();
		// crea ed avvia i consumatori
		ArrayList<Thread> workers = new ArrayList<Thread>(K);
		for (int i = 0; i < K; i++) {
			Thread c = new Thread(new Worker(directories));
			// ogni consumatore viene aggiunto alla lista workers
			workers.add(c);
			c.start();
		}

		for (int i = 0; i < K; i++){
			try {
				workers.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
		}
	}
}


