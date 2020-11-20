import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Scrivere un programma JAVA che implementa un server HTTP che gestisce richieste di trasferimento di file di diverso tipo (es. immagini jpeg, gif) provenienti da un browser web.
 *
 * Il server
 * - sta in ascolto su una porta nota al client (es. 6789)
 * - gestisce richieste HTTP di tipo GET alla Request URL localhost:port/filename
 *
 * Ulteriori indicazioni
 * - le connessioni possono essere non persistenti.
 * - usare le classi Socket e ServerSocket per sviluppare il programma server
 * - per inviare al server le richieste, utilizzare un qualsiasi browser
 *
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
class WebServer {
	/**
	 * numero di worker utilizzati dal threadpool (nel caso in cui venga dichiarato "fixed")
	 */
	final static int N_WORKERS = 1;
	/**
	 * porta di default
	 */
	final static int DEFAULT_PORT = 6789;

	public static void main(String[] args) {
		int myPort = DEFAULT_PORT;

		if (args.length > 0) {				// la porta è stata passata come argomento al programma
			try {
				myPort = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("Il numero di porta deve essere un intero");
				System.exit(-1);
			}
		}

		// dichiara il threadpool che si occupa della gestione delle richieste
		ExecutorService pool = Executors.newFixedThreadPool(N_WORKERS);
		//ExecutorService pool = Executors.newCachedThreadPool();
		
		try (
			// welcoming socket
			ServerSocket server = new ServerSocket(myPort);
		) {
			System.out.printf("Web server in attesa sulla porta %d\n", myPort);
			while (true) {
				// rimane in attesa di una richiesta di connessione
				Socket client = server.accept();
				// invio al threadpool la richiesta di gestione del client
				pool.execute(new RequestManager(client));
			}			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			// terminazione "gentile" del threadpool
			pool.shutdown();
			try {
				while(!pool.isTerminated())
					pool.awaitTermination(60, TimeUnit.SECONDS);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}