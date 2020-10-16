package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/10
 * @versione	1.1
 */

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
/*
    Il Laboratorio implementa le gestione effettiva delle postazioni, senza pensare alla concorrenza
 */

public class Laboratorio {
    /** NON è una classe Thread-Safe
     * per ogni metodo:
     * @REQUIRES esecuzione in mutua esclusione, che DEVE essere garantita dal chiamante
     */

    private final int LAB_SIZE = 20;

    // costanti per favorire la human readability
    private final boolean AVAILABLE = true;
    private final boolean NOT_AVAILABLE = false;

    class PC {
        boolean state;
        ArrayList<Long> waiters;
    }

    // postazione[i] = TRUE se la postazione è libera, FALSE altrimenti
    // l'array è final poichè il riferimento all'array non voglio che cambi una volta inizializzato dal costruttore
    private final PC postazioni[];

    public Laboratorio() {
        postazioni = new PC[LAB_SIZE];
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni[i] = new PC();
            postazioni[i].state = AVAILABLE;
            postazioni[i].waiters = new ArrayList<>();
        }
    }

/** PRENOTAZIONI
 *  Overload del metodo book
 */
    /** valgono per tutti i book()
     * @effects		occupano i/il pc richiesto
     * @param p/t/s oggetto Utente chiamante
     */
    public void book(Professore p) {
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni[i].state = NOT_AVAILABLE;
        }
    }

    /**
     * prova ad occupare uno specifico.
     * @param t     Tesista
     * @param index indice del pc da occupare
     * @return  true iff se riesce a prentoarlo, false altrimenti
     */
    public void book(Tesista t, int index) {
        postazioni[index].waiters.remove(Thread.currentThread().getId());
        postazioni[index].state = NOT_AVAILABLE;
    }

    /**
     * @effects     Occupa la postazione di indice "index"
     * @param s     Studente
     * @param index indice del pc che vuole occupare lo studente
     */
    public void book(Studente s, int index) {
        postazioni[index].state = NOT_AVAILABLE;
    }

/* Rilasci delle postazioni */

    /** valgono per tutti i leave()
     * @effects 	liberano i/il pc precedentemente occupato
     * @param p/t/s oggetto Utente chiamante
     */
    public void leave(Professore p) {
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni[i].state = AVAILABLE;
        }
    }

    /**
     * @param t		Tesista chiamante
     * @param index indice del pc occupato dal tesista
     */
    public void leave(Tesista t, int index) {
        postazioni[index].waiters.remove(Thread.currentThread().getId());
        postazioni[index].state = AVAILABLE;
    }

    /**
     * @param u		Studente chiamante
     * @param index indice del pc occupato dallo studente
     */
    public void leave(Utente u, int index) {
        postazioni[index].state = AVAILABLE;
    }

    /**
     * @param   s   Studente chiamante
     * @return  indice della prima postazione libera e non prenotata da tesisti, se ce ne sono
     *          altrimenti -1
     */
    public int findAvailable(Studente s) {
        for(int i = 0; i < LAB_SIZE; i++) {
            if(postazioni[i].state == AVAILABLE && postazioni[i].waiters.size() == 0)
                return i;
        }
        return -1;
    }

    /**
     * @return un indice random IN [0, LAB_SIZE-1]
     */
    public int getRandomPcIndex() {
        return ThreadLocalRandom.current().nextInt(LAB_SIZE);
    }

    /**
     * @param p Professore chiamante
     * @return  il numero di postazioni libere
     */
    public int computersAvailable(Professore p) {
        int cnt = 0;
        for(int i = 0; i < LAB_SIZE; i++) {
            if(postazioni[i].state == AVAILABLE)
                cnt++;
        }
        return cnt;
    }

    /**
     * @return  TRUE iff tutte le postazioni sono libere
     */
    public boolean isEmpty(Professore p) {
        if(computersAvailable(p) == LAB_SIZE)
            return true;
        else
            return false;
    }

    /**
     * @param index     indice del pc da controllare
     * @return          true iff il pc di indice index è libero
     */
    public boolean isPcFree(int index) {
        return this.postazioni[index].state;
    }

    public void waitForPc(Tesista t, int index) {
        postazioni[index].waiters.add(Thread.currentThread().getId());
    }
}
