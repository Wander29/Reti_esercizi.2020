/**
 * Tutor modella il tutor di un laboratorio. Si occupa delle gestione degli accessi ad esso.
 *
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class Tutor {
    final private Laboratorio lab;
    /**
     * tiene traccia del numero di professori in attesa
     */
    private int professoriAwaiting;
    /**
     * tiene traccia del numero di tesisti in attesa per ogni computer
     */
    final private int[] tesistiAwaiting;

    /**
     *
     * @param l laboratorio la cui gestione si vuole assegnare al tutor
     */
    public Tutor(Laboratorio l){
        this.lab = l;
        this.professoriAwaiting = 0;
        int n_computers = lab.getNcomputers();
        this.tesistiAwaiting = new int[n_computers];
        for(int i=0; i<n_computers; i++)
            this.tesistiAwaiting[i] = 0;
    }


    /**
     * effettua una richiesta di accesso al laboratorio da parte del professore p
     * @param p professore che richiede il laboratorio
     */
    public synchronized void professoreRichiedeLab(Professore p){
        try {
            // incrementa il numero di professori in attesa
            this.professoriAwaiting++;
            while (!lab.isFree()) {
                System.out.printf("Professore %d: in attesa del laboratorio\n", p.getMatricola());
                wait();
            }
            // decrementa il numero di professori in attesa
            this.professoriAwaiting--;
            lab.occupyAll();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * avvisa il tutore che il professore p ha rilasciato il laboratorio
     * @param p professore che rilascia il laboratorio
     */
    public synchronized void professoreRilasciaLab(Professore p){

        // rilascia tutti i computer del laboratorio
        lab.releaseAll();

        // risveglia tutti gli utenti in attesa
        notifyAll();
    }

    /**
     * effettua una richiesta di accesso al computer id_pc del laboratorio da parte del tesista t
     * @param t tesista
     * @param id_pc id del pc a cui il tesista vuole lavorare
     */
    public synchronized void tesistaRichiedeComputer(Tesista t, int id_pc){
        try {
            this.tesistiAwaiting[id_pc]++;
            while (this.professoriAwaiting > 0 || !lab.isAvailable(id_pc)) {
                System.out.printf("Tesista %d: in attesa del computer %d\n", t.getMatricola(), id_pc);
                wait();
            }
            this.tesistiAwaiting[id_pc]--;
            lab.occupyComputer(id_pc);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * avvisa il tutore che il tesista t ha terminato il lavoro al computer id_pc
     * @param t tesista
     * @param id_pc id del pc rilasciato dal tesista
     */
    public synchronized void tesistaRilasciaComputer(Tesista t, int id_pc){

        // rilascia il computer id_pc del laboratorio
        lab.releaseComputer(id_pc);

        // risveglia tutti gli utenti in attesa
        notifyAll();
    }


    /**
     *
     * @return  l'id del primo computer disponibile (se esiste e non ci sono tesisti in attesa su quel computer)
     *          -1 se nessun computer è disponibile
     */
    private synchronized int getComputerLiberoPerStudente(){
        for(int i=0; i<lab.getNcomputers(); i++){
            if (lab.isAvailable(i) && this.tesistiAwaiting[i]==0){
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
    public synchronized int studenteRichiedeComputer(Studente s){
        int id_pc = -1;
        try {
            while (this.professoriAwaiting > 0 || this.getComputerLiberoPerStudente() == -1) {
                System.out.printf("Studente %d: in attesa di un computer libero\n", s.getMatricola());
                wait();
            }
            id_pc = this.getComputerLiberoPerStudente();
            lab.occupyComputer(id_pc);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id_pc;
    }

    /**
     * avvisa il tutore che lo studente s ha terminato il lavoro al computer id_pc
     * @param s studente
     * @param id_pc id del pc rilasciato dallo studente
     */
    public synchronized void studenteRilasciaComputer(Studente s, int id_pc){

        // rilascia il computer id_pc del laboratorio
        lab.releaseComputer(id_pc);

        // risveglia tutti gli utenti in attesa
        notifyAll();
    }
}
