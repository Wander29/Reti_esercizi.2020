import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contatore modella un task che si occupa di contare il numero di movimenti per causale
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class Contatore implements Runnable {
	/**
	 * conto corrente di cui si vuole contare le occorrenze
	 */
	private final ContoCorrente cc;
	/**
	 * contatore globale delle occorrenze
	 */
	private final ConcurrentHashMap<Causale, Long> globalCounter;
	/**
	 * contatore locale delle occorrenze
	 */
	private final HashMap<Causale, Integer> localCounter;

	/**
	 *
	 * @param cc conto corrente
	 * @param counters contatore globale delle occorrenze
	 */
	public Contatore(ContoCorrente cc, ConcurrentHashMap<Causale, Long> counters) {
		super();
		this.cc = cc;
		this.globalCounter = counters;
		localCounter = new HashMap<Causale, Integer>();
		for (Causale c: Causale.values()){
			localCounter.put(c, 0);
		}
	}


	@Override
	public void run() {
		int n = cc.getN_movimenti();

		// calcola le occorrenze locali delle causali
		for(int i=0; i<n; i++){
			Movimento m = cc.getMovimento(i);
			Causale c = m.getCausale();
			localCounter.put(c, localCounter.get(c)+1);
		}

		// comunica le occorrenze ottenute localmente al contatore globale
		for (Map.Entry<Causale, Integer> e: localCounter.entrySet()) {
			globalCounter.computeIfPresent(e.getKey(), (c, k) -> k + e.getValue());
		}
	}
}
