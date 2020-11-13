import java.io.Serializable;
import java.util.LinkedList;

/**
 * ContoCorrente modella un conto corrente contenente un insieme di movimenti
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class ContoCorrente implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * nome del correntista
	 */
	private String correntista;
	/**
	 * lista contenente i movimenti del conto corrente
	 */
	private LinkedList<Movimento> l_movimenti;
	/**
	 * numero dei movimenti presenti nel conto corrente
	 */
	private int n_movimenti;

	public ContoCorrente(){}

	public ContoCorrente(String correntista) {
		this.correntista = correntista;
		this.l_movimenti = new LinkedList<>();
		this.n_movimenti=0;
	}


	/**
	 *
	 * @param index indice del movimento da ritornare
	 * @return movimento in posizione index
	 */
	public Movimento getMovimento(int index){
		return this.l_movimenti.get(index);
	}


	/*
	 * @effects aggiunge un nuovo movimento al conto corrente ed aggiorna il numero totale di movimenti
	 */

	/**
	 * aggiunge il movimento m al conto corrente
	 *
	 * @param m movimento da aggiungere
	 */
	public void addMovimento(Movimento m){
		this.l_movimenti.add(m);
		this.n_movimenti++;
	}


	/**
	 * stampa il conto corrente con i relativi movimenti
	 */
	public void printInfo(){
		System.out.println("Conto corrente di: " + this.correntista);
		for (Movimento movimento : l_movimenti) {
			movimento.printInfo();
		}

	}

	/**
	 *
	 * @return ContoCorrente#correntista
	 */
	public String getCorrentista(){
		return this.correntista;
	}
	

	/**
	 *
	 * @return ContoCorrente#n_movimenti
	 */
	public int getN_movimenti() {
		return n_movimenti;
	}
}
