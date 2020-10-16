import java.util.LinkedList;
import java.util.concurrent.*;


/**
 * UfficioPostale modella il comportamento di un ufficio postale
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class UfficioPostale implements Ufficio{
	protected final int numeroSportelli = 4;
	protected final int dimensioneSalaSportelli;
	protected final LinkedList<Cliente> salaAttesa;
	protected final ArrayBlockingQueue<Runnable> salaSportelli;
	protected ExecutorService sportelli;

	/**
	 *
	 * @param k dimensione della sala degli sportelli
	 */
	public UfficioPostale(int k){
		this.dimensioneSalaSportelli = k;
		this.salaAttesa = new LinkedList<>();
		this.salaSportelli = new ArrayBlockingQueue<>(this.dimensioneSalaSportelli);
	}

	@Override
	public void aperturaUfficio(int n) {
		this.sportelli = new ThreadPoolExecutor(
				this.numeroSportelli,
				this.numeroSportelli,
				0L,
				TimeUnit.SECONDS,
				this.salaSportelli
		);

		System.out.println("Ufficio aperto");

		for(int i=0; i<n; i++){
			this.salaAttesa.add(new Cliente(i));
		}

	}


	@Override
	public void aperturaSportelli() {
		while(!salaAttesa.isEmpty()) {
			Cliente c = this.salaAttesa.peek();	// recupera la testa della lista senza rimuoverla
			try {

				//System.out.printf("Cliente numero %d: prova ad entrare nella sala sportelli, persone nella sala sportelli = %d \n", c.getNumero(), salaSportelli.size());

				sportelli.execute(c);

				// se la execute va a buon fine è possibile rimuovere l'item dalla lista
				salaAttesa.poll(); // recupera la testa della lista e la rimuove

				System.out.printf(
						"Cliente numero %d: entra nella sala sportelli, persone nella sala sportelli = %d \n",
						c.getNumero(),
						salaSportelli.size()
				);

			} catch (RejectedExecutionException e) {
				//System.out.printf("Cliente numero %d: posti seconda sala esauriti \n", c.getNumero());
			}
		}
	}

	@Override
	public void chiusuraUfficio() {
		sportelli.shutdown();
		try {
			while(!sportelli.isTerminated()) {
				sportelli.awaitTermination(5, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Tutte i clienti sono stati serviti");
	}

}