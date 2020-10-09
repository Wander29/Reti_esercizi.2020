/**
 * Writer modella lo scrittore, colui che incrementa il contatore
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class Writer implements Runnable {
    /**
     * riferimento ad un contatore instanza della classe Counter
     */
    private Counter c;

    /**
     *
     * @param c contatore
     */
    public Writer(Counter c){
        this.c = c;
    }

    @Override
    public void run() {
        c.increment();
    }
}
