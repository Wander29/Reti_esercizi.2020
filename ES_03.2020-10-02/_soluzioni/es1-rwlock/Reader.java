/**
 * Reader modella il lettore del contatore
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class Reader implements Runnable{
    /**
     * riferimento ad un contatore instanza della classe Counter
     */
    private Counter c;

    /**
     *
     * @param c contatore
     */
    public Reader(Counter c){
        this.c = c;
    }

    @Override
    public void run() {
        System.out.println(c.get());
    }
}
