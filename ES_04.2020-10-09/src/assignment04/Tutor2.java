package assignment04;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

/* il Tutor gestisce esclusivamente la concorrenza nell'acceso al laboratorio, la gestione effettiva di esso
* 		è demandata alla classe Laboratorio */

public class Tutor2 extends StrutturaLaboratorio {

	private final boolean DEBUG = false;
	private final Laboratorio lab; 		// laboratorio da gestire

	// "semafori" per effettuari veloci controlli sulle priorità
	private int waitingProf;
	private ArrayList<ArrayList<Long>> tesistiAwaiting;

	public Tutor2(Laboratorio l) {
		lab = l;

		// inizializzazione dei semafori
		this.waitingProf = 0;

		// per ogni postazione creo una lista d'attesa per i tesisti
		this.tesistiAwaiting = new ArrayList<>(this.LAB_SIZE);
		for(int i = 0; i < this.LAB_SIZE; i++) {
			this.tesistiAwaiting.add(new ArrayList<>());
		}
	}

/* Prenotazioni */

	/** valgono per tutti i book()
	 * @effects		Cercano di prenotare i pc richiesti
	 * @throws 		InterruptedException
	 */

	public synchronized void bookProf() throws InterruptedException {

		this.waitingProf++;
		while(!this.lab.isEmpty()) {
			this.wait();
		}
		this.waitingProf--;

		this.lab.bookProf(); // prenotazione effettiva del laboratorio, ovvero il professore occupa tutti i posti
	}
	
	public synchronized void bookTesi(long matricola, int index) throws InterruptedException {
		this.tesistiAwaiting.get(index).add(matricola);
		while (this.waitingProf > 0
					|| this.tesistiAwaiting.get(index).get(0) != matricola
					|| !this.lab.isPcFree(index)) {
			// finchè c'è un professore in attesa OR c'è un tesista in attesa davanti a me
			// 			|| il pc necessario a questo tesista è occupato
			if(DEBUG) {
				System.out.println("[TESI " + Thread.currentThread().getName() + "] PC tentato [" + index
						+ "]");
			}
			this.wait();
		}
		if(DEBUG) {
			System.out.println("[TESI " + Thread.currentThread().getName() + " PC post [" + index
					+ "] ]");
		}

		this.tesistiAwaiting.get(index).remove(matricola);
		this.lab.bookPC(index);
	}

	/**
	 * @returns l'indice del pc occupato dallo studente chiamante
	 */
	public synchronized int bookStud() throws InterruptedException {
		int index = -1;

	// finchè non ci sono pc disponibili OR non prenotati da tesisti OR ci son professori in attesa
		while (this.waitingProf > 0
					|| (index = this.findAvailablePCStud()) == -1 ) {
			if(DEBUG) {
				System.out.println("[STUD " + Thread.currentThread().getName() + " PC tentato [" + index
						+ "] ]");
			}
			this.wait();
		}

		this.lab.bookPC(index);
		return index; // ritorna l'indice del pc occupato
	}

/* Rilasci delle postazioni */

	/** valgono per tutti i leave()
	 * @effects 	liberano i/il pc precedentemente occupato e svegliano altri utenti
	 */
	public synchronized void leaveProf() {
		this.lab.leaveProf(); // rilascio effettivo di tutti i posti

		this.notifyAll();
	}

	/**
	 * @param index indice del pc occupato dall'utente
	 */
	public synchronized void leavePC(int index) {
		this.lab.leavePC(index);

		this.notifyAll();
	}

	/**
	 * @return indice del primo pc disponibile e su cui non ci sono tesisti in attasa
	 */
	public int findAvailablePCStud() {
		for(int i = 0; i < this.LAB_SIZE; i++) {
			if(this.tesistiAwaiting.get(i).isEmpty() && this.lab.isPcFree(i)) {
				return i;
			}
		}
		return -1;
	}

	public int identifyPcTesista() {
		return ThreadLocalRandom.current().nextInt(this.LAB_SIZE);
	}
}	
