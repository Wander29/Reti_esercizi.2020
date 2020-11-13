import java.util.*;

/**
 * WorkQueue modella una coda sincronizzata.
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class WorkQueue {

	private LinkedList<String> list;
	/**
	 * variabile booleana con la seguente semantica
	 * 		true se non ci sono più item da aggiungere
	 * 		false altrimenti
	 */
	private boolean done;

	/**
	 * numero di elementi presenti nella coda
	 */
	private int size;  		// number of directories in the queue

	public WorkQueue() {
		list = new LinkedList<String>();
		done = false;
		size = 0;
	}

	/**
	 * Aggiunge un item alla coda
	 *
	 * @param s stringa da aggiungere alla coda
	 */
	public synchronized void add(String s) {
		list.add(s);
		size++;
		notifyAll();
	}

	/**
	 * Preleva e ritorna un item dalla coda
	 *
	 * @return 	un item se la coda non è vuota
	 * 			null se la coda è vuota
	 */
	public synchronized String remove() {
		String s;
		while (!done && size == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
		}
		if (size > 0) {
			s = list.remove();
			size--;
			notifyAll();
		} else
			s = null;
		return s;
	}

	/**
	 * comunica che non ci sono più item da aggiungere alla coda
	 */
	public synchronized void finish() {
		done = true;
		notifyAll();
	}
}

