/**
 * Cliente modella un cliente dell'ufficio postale
 * @author Samuel Fabrizi
 * @version 1.1
 */

public class Cliente implements Runnable{
	private final int numero;

	/**
	 *
	 * @param n numero del cliente
	 */
	public Cliente(int n) {
		this.numero = n;
	}

	@Override
	public void run() {
		//la persona i-esima viene servita allo sportello per un tempo duration
		long duration = (long) (Math.random()*1000);
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			System.out.println("Sleep interrotta");
			return;
		}
		System.out.printf("Cliente numero %d: servito in %d ms\n", this.numero, duration);
	}

	/**
	 *
	 * @return Cliente#numero
	 */
	public int getNumero() {
		return numero;
	}

}