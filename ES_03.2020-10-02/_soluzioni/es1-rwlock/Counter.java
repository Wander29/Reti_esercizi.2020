/**
* Counter modella il contatore
* @author Samuel Fabrizi
* @version 1.0
*/

public class Counter {
    /**
     * rappresenta il contatore
     */
    private int contatore = 0;

    /**
     * incrementa di 1 unità il valore del contatore
     */
    public void increment(){
        contatore++;
    }

    /**
     *
     * @link Counter#contatore
     */
    public int get(){
        return contatore;
    }
}
