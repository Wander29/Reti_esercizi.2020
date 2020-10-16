/**
 * Ufficio definisce le azioni svolte da un generico ufficio
 * @author Samuel Fabrizi
 * @version 1.0
 */


public interface Ufficio {
	
	/**
	 * determina l'apertura di un ufficio
	 * @param n numero di clienti
	 */
	public void aperturaUfficio(int n);

	/**
	 * determina l'avvio dell'attività dell'ufficio
	 */
	public void aperturaSportelli();

	/**
	 * determina la chiusura dell'ufficio
	 */
	public void chiusuraUfficio();
	
}
