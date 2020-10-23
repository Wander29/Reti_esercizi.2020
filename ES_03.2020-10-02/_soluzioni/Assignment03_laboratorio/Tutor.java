import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Tutor modella il tutor di un laboratorio. Si occupa delle gestione degli accessi ad esso.
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class Tutor {
    final private Laboratorio lab;
    final private ReentrantLock lockLaboratorio;
    /**
     * variabile di condizione per l'attesa dei professori
     */
    final private Condition professoriAwaiting;
    /**
     * array di variabili di condizione per l'attesa dei tesisti
     */
    final private Condition[] tesistiAwaiting;
    /**
     * variabile di condizione per l'attesa degli studenti
     */
    final private Condition studentiAwaiting;

    /**
     *
     * @param l laboratorio la cui gestione si vuole assegnare al tutor
     */
    public Tutor(Laboratorio l){
        this.lab = l;
        this.lockLaboratorio = new ReentrantLock();

        this.professoriAwaiting = lockLaboratorio.newCondition();

        int n_computers = lab.getNcomputers();
        this.tesistiAwaiting = new Condition[n_computers];
        for(int i=0; i<n_computers; i++)
            this.tesistiAwaiting[i] = lockLaboratorio.newCondition();

        this.studentiAwaiting = lockLaboratorio.newCondition();

    }


    /**
     * effettua una richiesta di accesso al laboratorio da parte del professore p
     * @param p professore che richiede il laboratorio
     */
    public void professoreRichiedeLab(Professore p){
        lockLaboratorio.lock();
        try {
            while (!lab.isFree()) {
                System.out.printf("Professore %d: in attesa del laboratorio\n", p.getMatricola());
                professoriAwaiting.await();
            }
            lab.occupyAll();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lockLaboratorio.unlock();
        }

    }

    /**
     * avvisa il tutore che il professore p ha rilasciato il laboratorio
     * @param p professore che rilascia il laboratorio
     */
    public void professoreRilasciaLab(Professore p){
        lockLaboratorio.lock();

        // rilascia tutti i computer del laboratorio
        lab.releaseAll();

        // controlla se esiste qualche professore in attesa
        if (lockLaboratorio.hasWaiters(professoriAwaiting)){
            professoriAwaiting.signal();
        }
        else{
            // controlla se esiste qualche tesista in attesa
            for(Condition t: tesistiAwaiting){
                t.signal();
            }
            // sveglia tutti gli studenti in attesa
            studentiAwaiting.signalAll();
        }
        lockLaboratorio.unlock();
    }

    /**
     * effettua una richiesta di accesso al computer id_pc del laboratorio da parte del tesista t
     * @param t tesista
     * @param id_pc id del pc a cui il tesista vuole lavorare
     */
    public void tesistaRichiedeComputer(Tesista t, int id_pc){
        lockLaboratorio.lock();
        try {
            while (lockLaboratorio.hasWaiters(professoriAwaiting) || !lab.isAvailable(id_pc)) {
                System.out.printf("Tesista %d: in attesa del computer %d\n", t.getMatricola(), id_pc);
                tesistiAwaiting[id_pc].await();
            }
            lab.occupyComputer(id_pc);
        }z
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lockLaboratorio.unlock();
        }

    }

    /**
     * avvisa il tutore che il tesista t ha terminato il lavoro al computer id_pc
     * @param t tesista
     * @param id_pc id del pc rilasciato dal tesista
     */
    public void tesistaRilasciaComputer(Tesista t, int id_pc){
        lockLaboratorio.lock();

        // rilascia tutti i computer del laboratorio
        lab.releaseComputer(id_pc);

        // controlla se esiste qualche professore in attesa
        if (lockLaboratorio.hasWaiters(professoriAwaiting)){
            professoriAwaiting.signal();
        } else {
            // controlla se esiste qualche tesista in attesa del computer rilasciato
            if (lockLaboratorio.hasWaiters(tesistiAwaiting[id_pc])){
                tesistiAwaiting[id_pc].signal();
            } else {
                // sveglia uno studente in attesa
                studentiAwaiting.signal();
                }
        }
        lockLaboratorio.unlock();
    }


    /**
     *
     * @return  l'id del primo computer disponibile (se esiste e non ci sono tesisti in attesa su quel computer)
     *          -1 se nessun computer è disponibile
     */
    public int getComputerLiberoPerStudente(){
        for(int i=0; i<lab.getNcomputers(); i++){
            if (lab.isAvailable(i) && !lockLaboratorio.hasWaiters(tesistiAwaiting[i])){
                return i;
            }
        }
        return -1;
    }

    /**
     * effettua una richiesta di accesso al primo computer disponibile da parte dello studente s
     * @param s studente
     * @return id del pc acquisito dallo studente s
     */
    public int studenteRichiedeComputer(Studente s){
        lockLaboratorio.lock();
        int id_pc = -1;
        try {
            while (lockLaboratorio.hasWaiters(professoriAwaiting) || this.getComputerLiberoPerStudente() == -1) {
                System.out.printf("Studente %d: in attesa di un computer libero\n", s.getMatricola());
                studentiAwaiting.await();
            }
            id_pc = this.getComputerLiberoPerStudente();
            lab.occupyComputer(id_pc);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lockLaboratorio.unlock();
        }
        return id_pc;
    }

    /**
     * avvisa il tutore che lo studente s ha terminato il lavoro al computer id_pc
     * @param s studente
     * @param id_pc id del pc rilasciato dallo studente
     */
    public void studenteRilasciaComputer(Studente s, int id_pc){
        lockLaboratorio.lock();

        // rilascia tutti i computer del laboratorio
        lab.releaseComputer(id_pc);

        // controlla se esiste qualche professore in attesa
        if (lockLaboratorio.hasWaiters(professoriAwaiting)){
            professoriAwaiting.signal();
        } else {
            // controlla se esiste qualche tesista in attesa del computer rilasciato
            if (lockLaboratorio.hasWaiters(tesistiAwaiting[id_pc])){
                tesistiAwaiting[id_pc].signal();
            } else {
                // sveglia uno studente in attesa
                studentiAwaiting.signal();
            }
        }
        lockLaboratorio.unlock();
    }

}
