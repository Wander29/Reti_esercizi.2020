/**
 * Viaggiatore modella un viaggiatore che vuole acquistare un nuovo biglietto
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class Viaggiatore implements Runnable{
    private static final int MAX_ATTESA = 1000;
    /**
     * id univoco per identificare un viaggiatore
     */
    private final int id;

    /**
     *
     * @param id identificatore del viaggiatore
     */
    public Viaggiatore(int id){
        this.id = id;
    }

    /**
     *
     * @link Viaggiatore#id
     */
    public int getId() {
        return id;
    }

    @Override
    public void run() {
        System.out.printf("Viaggiatore %d: Sto acquistando un biglietto\n", this.id);
        try {
            Thread.sleep((int) (Math.random() * MAX_ATTESA));   // attendo per un tempo t con 0 < t < MAX_ATTESA
        }
        catch (InterruptedException e){
            System.out.println("Viaggiatore " + this.id + " interrotto");
        }
        System.out.printf("Viaggiatore %d: Ho acquistato un biglietto\n", this.id);
    }
}
