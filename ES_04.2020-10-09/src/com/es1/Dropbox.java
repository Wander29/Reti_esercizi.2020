package com.es1;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Scrivere un programma in cui alcuni thread generano e consumano numeri interi da una risorsa
 * condivisa (chiamata Dropbox) che ha capacità 1.
 * 1. Nella classe Dropbox fornita in allegato il buffer di dimensione 1 è gestito tramite una variabile
 * intera num. La classe offre un metodo take per consumare il numero e svuotare il buffer e un
 * metodo put per inserire un nuovo valore se il buffer è vuoto.
 * 2. Definire un task Consumer il cui metodo costruttore prende in ingresso un valore booleano
 * (true per consumare valori pari e false per valori dispari) e il riferimento ad un’istanza di
 * Dropbox. Nel metodo run invoca il metodo take sull’istanza di Dropbox.
 * 3. Definire un task Producer il cui metodo costruttore prende in ingresso il riferimento ad
 * un’istanza di Dropbox. Nel metodo run genera un intero in modo random, nel range [0,100), e
 * invoca il metodo put sull’istanza di Dropbox.
 * 4. Definire una classe contenente il metodo main. Nel main viene creata un’istanza di Dropbox.
 * Vengono quindi creati 2 oggetti di tipo Consumer (uno true e uno false) e un oggetto di tipo
 * Producer, ciascuno eseguito da un thread distinto.
 * 5. Estendere la classe Dropbox (overriding dei metodi take e put) usando il costrutto del
 * monitor per gestire l’accesso di Consumer e Producer al buffer. Notare la differenza nell’uso
 * di notify vs notifyall
 */

/**
 * Dropbox modella un bounded buffer di dimensione 1
 * @author Samuel Fabrizi
 * @version 1.1
 */

public class Dropbox {
	/**
		full è uguale a true se il buffer è pieno, false altrimenti
	 */
	protected boolean full = false;
	/**
	 * num valore del buffer (utile solo se il buffer è pieno)
	 */
	protected int num;

	/**
	 * Attende che il buffer contenga un numero, poi lo recupera e lo ritorna
	 * @param e indica l'interesse a consumare un numero pari o dispari
	 * 			se e == True il numero contenuto è pari, altrimenti è dispari
	 * @return numero consumato
	 */
	public int take(boolean e) {
		/* La seguente espressione equivale a:
		 * if (e == true) {
		 * 		s = "Pari"
		 * }
		 * else {
		 * 		s = "Dispari
		 * }
		 */
		String s = e ? "Pari" : "Dispari";

		while (!full || e == (num % 2 != 0)) { //num non è quello cercato
			System.out.println("Attendi per: " + s);
			try {
				Thread.sleep((long) (Math.random()*100));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		try {
			Thread.sleep((long) (Math.random()*1000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println(s + " <-> " + num);
		full = false;
		return num;
	}

	/**
	 * Attende che il buffer sia vuoto, poi inserisce n all'interno di esso
	 * @param n intero da inserire nel buffer
	 */
	public void put(int n) {
		while (full) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Producer ha inserito " + n);
		num = n;
		full = true;
	}
}