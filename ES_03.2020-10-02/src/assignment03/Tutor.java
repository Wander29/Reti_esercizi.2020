package assignment03;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* il Tutor gestisce esclusivamente la concorrenza */

public class Tutor {
	private final Laboratorio lab;

	private final Lock mtx;
	private final Condition condProf;
	private final Condition condTesi;
	private final Condition condStud;
	
	private int waitingProf;
	private int waitingTesi;
	private int waitingStud;
	
	public Tutor(Laboratorio l) {
		lab = l;
		mtx = new ReentrantLock();
		
		condProf = mtx.newCondition();
		condTesi = mtx.newCondition();
		condStud = mtx.newCondition();
		
		this.waitingProf = 0;
		this.waitingTesi = 0;
		this.waitingStud = 0;
	}

/* Prenotazioni */
	public boolean book(Professore p) {
		mtx.lock();

		waitingProf++;
		while(waitingProf > 1 || !lab.isEmpty()) {
			try {
				condProf.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-PROF] Sleep interrotta");
				return false;
			}
		}
		waitingProf--;

		lab.book(p);

		mtx.unlock();
		return true;
	}
	
	public boolean book(Tesista t) {
		
		mtx.lock();
		waitingTesi++;
		
		while(waitingProf > 0 || waitingTesi > 1 || lab.isTesiPCBusy()) {
			try {
				condTesi.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-TESI] Sleep interrotta");
				return false;
			}
		}

		lab.book(t);
		
		mtx.unlock();
		return true;
	}
	
	public boolean book(Studente s) {
		mtx.lock();
		
		waitingStud++;
		while(waitingProf > 0 || waitingStud > 1 || lab.isFull()) {
			try {
				condStud.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-STUD] Sleep interrotta");
				return false;
			}
		}
		waitingStud--;

	/* scelta (casuale) del pc da occupare, se ci sono tesisti in attesa prende ugualmente il pc_tesi
	* 	si presume che venendo riattivati prima questi ultimi siano in grado di occupare il posto prima degli
	* 	studenti
* 	Altre soluzioni avrebbero introdotto possibile Starvation degli studenti o attesa della fine dell'uso
* 		dei tesisti, cosa contraria alle specicfiche (studenti e tesisti dovrebbero convivere nel lab) */

		/*
		int pc_scelto;
		do {
			pc_scelto = (int) Math.random() * this.lab_size;
		} while(pc[pc_scelto] == StatoPC.BUSY || (waitingTesi > 0 && pc_scelto == tesi_pc));
		n_free_pc--;
		*/
		lab.book(s);

		mtx.unlock();
		return true;
	}

/* Rilasci delle postazioni */
	public boolean leave(Professore p) {
		mtx.lock();
		lab.leave(p);

		if(waitingProf != 0) {
			condProf.signal();
		} else {
			if(waitingTesi != 0) {
				condTesi.signal();
			}
			if(waitingStud != 0) {
				condStud.signalAll();
			}
		}

		mtx.unlock();
		return true;
	}

	public boolean leave(Tesista t) {
		mtx.lock();
		lab.leave(t);

		if(waitingProf != 0) {
			condProf.signal();
		} else {
			if(waitingTesi != 0) {
				condTesi.signal();
			}
			if(waitingStud != 0) {
				condStud.signal();
			}
		}

		mtx.unlock();
		return true;
	}

	public boolean leave(Studente s) {
		mtx.lock();
		lab.leave(s);

		if(waitingProf != 0) {
			condProf.signal();
		} else {
			if(waitingTesi != 0) {
				condTesi.signal();
			}
			if(waitingStud != 0) {
				condStud.signal();
			}
		}

		mtx.unlock();
		return true;
	}
}	
