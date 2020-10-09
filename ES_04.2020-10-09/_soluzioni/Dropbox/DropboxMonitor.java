import java.util.concurrent.ThreadLocalRandom;

/**
 * DopboxMonitor modella un bounded buffer di dimensione 1 utilizzando il meccanismo dei monitor
 * @author Samuel Fabrizi
 * @version 1.1
 */
class DropboxMonitor extends Dropbox{

	@Override
	public synchronized int take(boolean e) {
		String s = e ? "Pari" : "Dispari";

		while (!full || e == (num % 2 != 0)) { // num non è quello cercato
			try {
				System.out.println("Attendi per: " + s);
				this.wait();
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
		this.notifyAll();
		return num;
	}

	@Override
	public synchronized void put(int n) {
		while (full) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Producer ha inserito " + n);
		num = n;
		full = true;
		this.notify();
		// this.notifyAll();
	}
}