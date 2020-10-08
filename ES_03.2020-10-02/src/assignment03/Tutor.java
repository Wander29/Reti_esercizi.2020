package assignment03;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/08
 * @versione	1.0
 */

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* il Tutor gestisce esclusivamente la concorrenza nell'acceso al laboratorio, la gestione effettiva di esso
* 		è demandata alla classe Laboratorio */

public class Tutor {
	private final Laboratorio lab; 		// laboratorio da gestire

	private final Lock mtx;				// lock del Tutor, usata per controllare le sezioni critiche qui presenti

	// Variabili di condizione finalizzate alla sincronizzazione degli utenti
	private final Condition condProf;
	private final Condition condTesi;
	private final Condition condStud;

	// "semafori" per effettuari veloci controlli sulle priorità
	private int waitingProf;
	private int waitingTesi;
	private int waitingStud;
	
	public Tutor(Laboratorio l) {
		lab = l;
		mtx = new ReentrantLock();
		
		condProf = mtx.newCondition();
		condTesi = mtx.newCondition();
		condStud = mtx.newCondition();

		// inizializzazione dei semafori
		this.waitingProf = 0;
		this.waitingTesi = 0;
		this.waitingStud = 0;
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
		mtx.lock();

		waitingProf++;
		while(!lab.isEmpty()) { // finchè non sono disponibili tutti i posti, aspetta
			condProf.await();
		}
		waitingProf--;

		lab.book(p); // prenotazione effettiva del laboratorio, ovvero il professore occupa tutti i posti

		mtx.unlock();
	}
	
	public void book(Tesista t) throws InterruptedException {
		mtx.lock();

		waitingTesi++;
		while(waitingProf > 0 || lab.isTesiPCBusy()) { // finchè c'è un professore in attesa OR il pc da tesisti è occupato
			condTesi.await();
		}
		waitingTesi--;

		lab.book(t);
		
		mtx.unlock();
	}

	/**
	 * @return l'indice del pc occupato dallo studente chiamante
	 */
	public int book(Studente s) throws InterruptedException {
		mtx.lock();
		
		waitingStud++;
		int index = 0;
		// finchè non ci sono pc disponibili OR ci son professori in attesa OR l'unico pc disponibile sarebbe quello dei tesisti
		while
		( 		waitingProf > 0
				|| (index = lab.findAvailable()) == -1
				|| (index == lab.getTesi_pcIndex() && lab.computersAvailable() == 1)
		) {
			condStud.await();
		}
		waitingStud--;

		lab.book(s, index); // passo anche l'index così da poterlo ripassare al momento del rilascio della postazione

		mtx.unlock();
		return index; // ritorna l'indice del pc occupato
	}

/* Rilasci delle postazioni */

	/** valgono per tutti i leave()
	 * @effects 	liberano i/il pc precedentemente occupato e svegliano altri utenti, in ordine di
	 * 				priorità: prima i Professori, poi i tesisti poi gli studenti
	 * @param p/t/s oggetto Utente chiamante
	 */
	public void leave(Professore p) {
		mtx.lock();

		lab.leave(p); // rilascio effettivo di tutti i posti

		if(waitingProf != 0) {
			condProf.signal(); // se c'è un professore in attesa lo sveglio per primo
		} else {
			if(waitingTesi != 0) { // altrimenti, provo prima a svegliare un tesista
				condTesi.signal();
			}
			if(waitingStud != 0) { // nel contempo sveglio TUTTI gli studenti, dato che libero tutto il pc
				condStud.signalAll();
			}
		}

		mtx.unlock();
	}

	public void leave(Tesista t) {
		mtx.lock();

		lab.leave(t); // non serve l'indice, c'è 1 solo PC da tesisti

		if(waitingProf != 0) {
			condProf.signal();
		}
		else if(waitingTesi != 0) {
			condTesi.signal();
		}
		else {
			condStud.signal(); // qui sveglio solo UNO studente, dato che si libera un singolo posto
		}

		mtx.unlock();
	}

	/**
	 * @param s		oggetto Studente chiamante
	 * @param index indice del pc occupato dallo studente
	 */
	public void leave(Studente s, int index) {
		mtx.lock();

		lab.leave(s, index); // passo anche l'indice

		if(waitingProf != 0) {
			condProf.signal();
		}
		// se lo studente si trovava sul pc da tesisti è bene che provi a svegliare un tesista prima di uno studente
		else if(index == lab.getTesi_pcIndex() && waitingTesi != 0) {
			condTesi.signal();
		}
		else {
			condStud.signal();
		}

		mtx.unlock();
	}
}	
