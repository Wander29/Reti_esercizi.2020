/**
 * Scrivere un programma in cui alcuni thread generano e consumano numeri interi da una risorsa condivisa (chiamata Dropbox) che ha capacità 1.
 *     1. Nella classe Dropbox fornita il buffer di dimensione 1 è gestito tramite una variabile intera num. La classe offre un metodo take per consumare il numero e svuotare il buffer e un metodo put per inserire un nuovo valore se il buffer è pieno. Il metodo take prende in ingresso un booleano per indicare l’interesse a consumare un numero pari (true) o dispari (false).
 *     2. Definire un task Producer il cui metodo costruttore prende in ingresso un valore booleano (true per valori pari e false per valori dispari) e il riferimento ad un’istanza di Dropobox. Nel metodo run invoca il metodo take di un oggetto Dropbox.
 *     3. Definire un task Producer il cui metodo costruttore prende in ingresso il riferimento ad un’istanza di Dropbox. Nel metodo run genera un intero in modo random e invoca il metodo put di un oggetto Dropbox.
 *     4. Definire una classe contenente il metodo main. Nel main viene creata un’istanza di Dropbox. Vengono quindi creati 2 oggetti di tipo Consumer (uno true e uno false) e un oggetto di tipo Producer, ciascuno eseguito da un thread distinto.
 *     5. Estendere la classe Dropbox usando il costrutto del monitor per gestire l’accesso di Consumer e Producer al buffer. Notare la differenza nell’uso di notify vs notifyall.
 */

/**
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class PCDropbox {

	public static void main(String[] args) {
		Dropbox db = new Dropbox();
		// Dropbox db = new DropboxMonitor();

		// creo i threads
		Thread c1 = new Thread(new Consumer(false, db));
		Thread c2 = new Thread(new Consumer(true, db));
		// Thread c3 = new Thread(new Consumer(true, db));
		Thread p1 = new Thread(new Producer(db));

		// avvio i threads
		c1.start();
		c2.start();
		// c3.start();
		p1.start();
	}
}