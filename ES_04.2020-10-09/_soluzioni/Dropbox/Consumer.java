/**
 * Consumer modella un consumatore che "consuma" un numero in Dropbox.
 * Il consumatore puà essere interessato ai numeri pari oppure (exclusive or) ai numeri dispari
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class Consumer implements Runnable {
	private Dropbox dropbox;
	/**
	 * even indica se il consumatore è interessato a numeri pari (true) o dispari (false)
	 */
	private boolean even;

	 /**
	 * @param e true se il consumatore è interessato a numeri pari, false altrimenti
	 * @param d instanza della classe Dropbox
	 */
	public Consumer(boolean e, Dropbox d) {
		even= e;
		dropbox= d;
	}


	@Override
	public void run() {
		while (true) {
			dropbox.take(even);
		}

	}
}