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
	public void book(Professore p) {
		mtx.lock();

		waitingProf++;
		while(!lab.isEmpty()) {
			try {
				condProf.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-PROF] Sleep interrotta");
				return;
			}
		}
		waitingProf--;

		lab.book(p);

		mtx.unlock();
	}
	
	public void book(Tesista t) {
		mtx.lock();

		waitingTesi++;
		while(waitingProf > 0 || lab.isTesiPCBusy()) {
			try {
				condTesi.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-TESI] Sleep interrotta");
				return;
			}
		}
		waitingTesi--;

		lab.book(t);
		
		mtx.unlock();
	}
	
	public int book(Studente s) {
		mtx.lock();
		
		waitingStud++;
		int index;
		while( 		(index = lab.findAvailable()) == -1
				|| 	waitingProf > 0
				|| (index == lab.getTesi_pcIndex() && lab.computersAvailable() == 1) ) {
			try {
				condStud.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-STUD] Sleep interrotta");
				return -1;
			}
		}
		waitingStud--;

		lab.book(s, index);

		mtx.unlock();
		return index;
	}

/* Rilasci delle postazioni */
	public void leave(Professore p) {
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
	}

	public void leave(Tesista t) {
		mtx.lock();
		lab.leave(t);

		if(waitingProf != 0) {
			condProf.signal();
		}
		else if(waitingTesi != 0) {
			condTesi.signal();
		}
		else {
			condStud.signal();
		}

		mtx.unlock();
	}

	public void leave(Studente s, int index) {
		mtx.lock();
		lab.leave(s, index);

		if(waitingProf != 0) {
			condProf.signal();
		}
		else if(index == lab.getTesi_pcIndex() && waitingTesi != 0) {
			condTesi.signal();
		}
		else {
			condStud.signal();
		}

		mtx.unlock();
	}
}	
