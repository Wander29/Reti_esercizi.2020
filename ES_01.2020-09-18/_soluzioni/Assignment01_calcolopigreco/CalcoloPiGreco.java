/**
 * CalcoloPiGreco modella il thread che esegue il calcolo approssimato di π
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class CalcoloPiGreco extends Thread {

	/**
	 * valore approssimato di π
	 */
	private double pi;
	/**
	 * accuracy (stopping condition)
	 */
	private final double acc;

	/**
	 *
	 * @param acc accuracy
	 */
	public CalcoloPiGreco(double acc){
		this.pi = 0;
		this.acc = acc;
	}
	
	@Override
	public void run() {
		int i, div;
		i = 0;
		div = 1;
		// esecuzione della serie di Gregory-Leibniz
		do
		{	if (i % 2 == 0)
				pi = pi + 4.0 / div;
			else
				pi = pi - 4.0/ div;
			i++;
			div = div + 2;
		} while (Math.abs(Math.PI-pi) >= acc && !Thread.interrupted());	// stoppping condition

		// System.out.printf("Valore di pi greco approssimato %f\n", this.pi);
	}

	/**
	 *
	 * @link CalcoloPiGreco#pi
	 */
	public double getPi() {
		return pi;
	}
}