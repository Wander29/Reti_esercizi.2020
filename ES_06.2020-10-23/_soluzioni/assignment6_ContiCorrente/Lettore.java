import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Lettore modella il lettore che legge del file json e passa i conti corrente (uno per volta) ai "contatori".
 * Il lettore tiene un riferimento al contatore globale delle occorrenze
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class Lettore extends Thread {
	/**
	 * dimensione del buffer
	 */
	private static final int BUFF_SIZE = (int) Math.pow(2, 30);; // 1 048 5

	/**
	 * file da cui leggere i conti corrente
	 */
	private final File file;

	/**
	 * threadpool per contare le occorrenze delle causali
	 */
	private final ExecutorService pool;

	/**
	 * contatore globale delle occorrenze (condiviso dai thread)
	 */
	private final ConcurrentHashMap<Causale, Long> globalCounter;


	/**
	 *
	 * @param input file da cui leggere
	 */
	public Lettore(File input) {
		this.file = input;
		pool = Executors.newCachedThreadPool();
 		globalCounter = new ConcurrentHashMap<Causale, Long>();
		Causale[] causali = Causale.values();

		for (Causale c: causali) {
			globalCounter.put(c, 0L);
		}
	}

	@Override
	public void run() {
		System.out.println("Lettore: inzio lettura dei contocorrenti");
		ByteBuffer buffer = ByteBuffer.allocate(BUFF_SIZE);

		try ( FileChannel inChannel= FileChannel.open(Paths.get(this.file.getPath()), StandardOpenOption.READ) ) {
			boolean stop = false;
			while (!stop) {
				if (inChannel.read(buffer) == -1){
					stop = true;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		/*
		 * rende visibile all'ObjectMapper gli attributi privati della classe di cui l'oggetto da deserializzare
		 * ne è l'istanza
		 */
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		// configura la formattazione della data
		mapper.setDateFormat(new SimpleDateFormat("dd-MMM-yy"));

		List<ContoCorrente> conti;
		try {
			 conti = mapper.reader()
					.forType(new TypeReference<List<ContoCorrente>>() {})
					.readValue(buffer.array());	 // lettura dal buffer
		}
		catch (IOException e){
			e.printStackTrace();
			return;
		}

		for(ContoCorrente cc : conti) {
			pool.execute(new Contatore(cc, globalCounter));
		  }

		// chiusura "gentile" del threadpool
		this.pool.shutdown();
		try {
			while(!this.pool.isTerminated())
				this.pool.awaitTermination(60, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * stampa i risultati del conteggio delle occorrenze
	 */
	public void printResults() {
		System.out.println("-------------");
		int total =0;
		for (Causale c: globalCounter.keySet()) {
			total += globalCounter.get(c);
			System.out.printf("Causale: %s, # occorrenze: %d \n", c.toString(), globalCounter.get(c));
		}
		System.out.println("-------------");
		System.out.println("Totale: " + total);
		System.out.println("-------------");
	}
}