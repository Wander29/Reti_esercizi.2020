import java.util.Random;

/**
 * Producer modella un produttore che inserisce un numero in Dropbox
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class Producer implements Runnable {

	private final Dropbox dropbox;


	/**
	 *
	 * @param d istanza della classe Dropobox
	 */
	public Producer(Dropbox d) {
		dropbox= d;
	}

	@Override
	public void run() {
		Random random= new Random();
		
		while (true) {
			int n = random.nextInt(100);
			dropbox.put(n);
		}
	}
}
