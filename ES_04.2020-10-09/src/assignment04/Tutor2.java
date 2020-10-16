package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/10
 * @versione	1.1
 */

/* il Tutor gestisce esclusivamente la concorrenza nell'acceso al laboratorio, la gestione effettiva di esso
* 		è demandata alla classe Laboratorio */

public class Tutor2 {
	private final boolean DEBUG = true;
	private final Laboratorio lab; 		// laboratorio da gestire

	// "semafori" per effettuari veloci controlli sulle priorità
	private int waitingProf;
	private int waitingTesi;
	private int waitingStud;

	private final Object mtx_prof;
	private final Object mtx_tesi;
	private final Object mtx_stud;

	public Tutor2(Laboratorio l) {
		mtx_prof = new Object();
		mtx_tesi = new Object();
		mtx_stud = new Object();

		lab = l;

		// inizializzazione dei semafori
		this.waitingProf = 0;
		this.waitingTesi = 0;
		this.waitingStud = 0;
	}

	public int identifyPcTesista() {
		return lab.getRandomPcIndex();
	}

/* Prenotazioni
*
*  Overload del metodo Book()
* 		Da notare che si deve passare come argomento per ogni funzione l'oggetto stesso */

	/** valgono per tutti i book()
	 * @effects		Cercano di prenotare i pc richiesti
	 * @param p/t/s oggetto Utente chiamante
	 * @throws 		InterruptedException
	 */
	public void book(Professore p) throws InterruptedException {

		synchronized (this) {
			waitingProf++;
		}
		while(true) { 			
			synchronized (this) {
				if(!lab.isEmpty(p)) {
					boolean flag = true;
				}
			}
			if()
			this.wait();
		}
		waitingProf--;

		lab.book(p); // prenotazione effettiva del laboratorio, ovvero il professore occupa tutti i posti
	}
	
	public synchronized void book(Tesista t, int index) throws InterruptedException {
		if(DEBUG) {
			System.out.println("[TESI " + Thread.currentThread().getName() + " PC pre [" + index
					+ "] ]");
		}
		lab.waitForPc(t, index);
		while (waitingProf > 0 || !lab.isPcFree(index)) { // finchè c'è un professore in attesa OR il pc da tesisti è occupato
			this.wait();
		}
		if(DEBUG) {
			System.out.println("[TESI " + Thread.currentThread().getName() + " PC post [" + index
					+ "] ]");
		}

		lab.book(t, index);
	}

	/**
	 * @return l'indice del pc occupato dallo studente chiamante
	 */
	public synchronized int book(Studente s) throws InterruptedException {
		int index = -1;

	// finchè non ci sono pc disponibili OR non prenotati da tesisti OR ci son professori in attesa
		while (waitingProf > 0 || (index = lab.findAvailable(s)) == -1 ) {
			if(DEBUG) {
				System.out.println("[STUD " + Thread.currentThread().getName() + " PC tentato [" + index
						+ "] ]");
			}
			this.wait();
		}
		System.out.println("[STUD " + Thread.currentThread().getName() + " PC preso [" + index
				+ "] ]");

		lab.book(s, index); // passo anche l'index così da poterlo ripassare al momento del rilascio della postazione
		return index; // ritorna l'indice del pc occupato
	}

/* Rilasci delle postazioni */

	/** valgono per tutti i leave()
	 * @effects 	liberano i/il pc precedentemente occupato e svegliano altri utenti, in ordine di
	 * 				priorità: prima i Professori, poi i tesisti poi gli studenti
	 * @param p/t/s oggetto Utente chiamante
	 */
	public synchronized void leave(Professore p) {
		lab.leave(p); // rilascio effettivo di tutti i posti

		this.notifyAll();
	}

	/**
	 * @param t		Tesista chiamante
	 * @param index indice del pc occupato dal tesista
	 */
	public synchronized void leave(Tesista t, int index) {
		lab.leave(t, index);
		this.notifyAll();
	}

	/**
	 * @param s		Studente chiamante
	 * @param index indice del pc occupato dallo studente
	 */
	public synchronized void leave(Studente s, int index) {
		lab.leave(s, index);
		this.notifyAll();
	}
}	
