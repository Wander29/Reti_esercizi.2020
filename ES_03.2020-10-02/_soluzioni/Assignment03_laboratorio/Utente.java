import java.util.Random;

/**
 * La classe astratta utente modella un generico utente del laboratorio
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public abstract class Utente implements Runnable {
    private final int matricola;
    protected final int k;  // numero di accessi che l'utente ha intenzione di fare
    protected long sleepTime;
    protected long workTime;
    protected Tutor tutor;

    /**
     *
     * @param m matricola
     * @param t tutor
     */
    public Utente(int m, Tutor t){
        this.tutor = t;
        this.matricola = m;
        Random rand = new Random();
        this.k = rand.nextInt(3) + 1;
        this.sleepTime = rand.nextInt(5000);
        this.workTime = rand.nextInt(3000);
    }

    @Override
    public void run() {
        try {
            for(int i=0; i<this.k; i++){
                // l'utente richiede al tutor l'accesso al laboratorio
                this.richiestaAccesso();
                Thread.sleep(this.workTime);
                // l'utente lascia il laboratorio
                this.rilascio();
                Thread.sleep(this.sleepTime);
            }

        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @return Utente#matricola
     */
    public int getMatricola() {
        return matricola;
    }

    /**
     *
     * @return Utente#k
     */
    public int getK() {
        return k;
    }

    /**
     * il metodo richiestaAccesso permette all'utente di richiedere al tutor l'accesso
     * al laboratorio in base alle proprie esigenze
     */
    abstract void richiestaAccesso();

    /**
     * il metodo rilascio permette all'utente di comunicare al tutor di aver terminato il lavoro
     * e conseguentemente che può lasciare il laboratorio
     */
    abstract void rilascio();
}
