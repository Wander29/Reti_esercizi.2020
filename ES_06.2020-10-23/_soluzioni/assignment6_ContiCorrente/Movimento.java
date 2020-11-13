import java.io.Serializable;
import java.util.Date;

/**
 * Movimento modella un movimento rappresentato da una data ed una causale.
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class Movimento implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * data in cui è avvenuto il movimento
	 */
	private Date data;
	/**
	 * causale del movimento
	 */
	private Causale causale;

	public Movimento(){ }

	/**
	 *
	 * @param data data in cui è avvenuto il movimento
	 * @param causale causale del movimento
	 */
	public Movimento(Date data, Causale causale) {
		this.data=data;
		this.causale=causale;
	}

	/**
	 *
	 * @return Movimento#data
	 */
	public Date getData(){
		return this.data;
	}

	/**
	 *
	 * @return Movimento#causale
	 */
	public Causale getCausale() {
		return causale;
	}

	/**
	 * stampa le informazioni del movimento
	 */
	public void printInfo() {
		System.out.println("\tData: "+ this.data);
		System.out.println("\tCausale: "+ this.causale.toString());
	}

}