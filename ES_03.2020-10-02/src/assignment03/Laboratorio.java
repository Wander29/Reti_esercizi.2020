package assignment03;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Laboratorio {
	
	
	// private ArrayList<Boolean> pc;
	private StatoPC pc[];
	
	private final Lock mtx;
	private final Condition condProf;
	private final Condition condTesi;
	private final Condition condStud;
	
	private int waitingProf;
	private int waitingTesi;
	private int waitingStud;
	
	/*  */
	
	private int n_free_pc;
	private int lab_size;
	private int tesi_pc;
	
	public Laboratorio(int num_pc) {
		// pc = new ArrayList<>(num_pc);
		pc = new StatoPC[num_pc];
		mtx = new ReentrantLock();
		
		condProf = mtx.newCondition();
		condTesi = mtx.newCondition();
		condStud = mtx.newCondition();
		
		this.waitingProf = 0;
		this.waitingTesi = 0;
		this.waitingStud = 0;
		
		this.n_free_pc = num_pc;
		this.lab_size = num_pc;
		
		tesi_pc = (int) (Math.random() * lab_size);
		System.out.println("PC TESI: [" + tesi_pc + "]");
	}
	
	public boolean bookProf() {
		mtx.lock();
		this.waitingProf++;
		
		while(waitingProf > 1 || n_free_pc != lab_size) {
			try {
				condProf.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-PROF] Sleep interrotta");
				return false;
			}
		}
		
		waitingProf--;
		// active prof?
		for(int i = 0; i < lab_size; i++) {
			if(pc[i] == StatoPC.FREE) {
				pc[i] = StatoPC.BUSY;
				n_free_pc--;
			}
		}
		assert(n_free_pc == 0);
		mtx.unlock();
		
		return true;
	}
	
	public boolean leave() {
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
		
		return true;
	}
	
	public boolean leaveProf() {
		
		for(int i = 0; i < lab_size; i++) {
			pc[i] = StatoPC.FREE;
			n_free_pc++;
		}
		assert(n_free_pc == lab_size);
		
		return leave();
	}
	
	public boolean bookTesi() {
		
		mtx.lock();
		waitingTesi++;
		
		while(waitingProf > 0 || waitingTesi > 1 || pc[tesi_pc] == StatoPC.BUSY) {
			try {
				condTesi.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-TESI] Sleep interrotta");
				return false;
			}
		}
		
		waitingTesi--;
		pc[tesi_pc] = StatoPC.BUSY;
		n_free_pc--;
		
		mtx.unlock();
		
		return true;
	}
	
	public boolean leaveTesi() {

		pc[tesi_pc] = StatoPC.FREE;
		n_free_pc++;
		
		return leave();
	}
	
	public boolean bookStud() {
		
		mtx.lock();
		
		waitingStud++;
		while(waitingProf > 0 || waitingTesi > 0 || waitingStud > 1 || n_free_pc == 0) {
			try {
				condStud.await();
			} catch (InterruptedException e) {
				System.out.println("[TUTOR-STUD] Sleep interrotta");
				return false;
			}
		}
		
		return true;
		
	}
	
	public boolean leaveStud() {
		
		
		return leave();
	}
	
}	
