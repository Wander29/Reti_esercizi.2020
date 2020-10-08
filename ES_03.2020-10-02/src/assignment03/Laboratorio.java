package assignment03;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/08
 * @versione	1.0
 */

import java.util.concurrent.ThreadLocalRandom;
/*
    Il Laboratorio implementa le gestione effettiva delle postazioni, senza pensare alla concorrenza
 */

public class Laboratorio {
    /** NON è una classe Thread-Safe
     * per ogni metodo:
     * @REQUIRES esecuzione in mutua esclusione, che DEVE essere garantita dal chiamante
     */

    // costanti per favorire la human readability
    private static final boolean AVAILABLE = true;
    private static final boolean NOT_AVAILABLE = false;

    private final int LAB_SIZE = 20;

    // postazione[i] = TRUE se la postazione è libera, FALSE altrimenti
    // l'array è final poichè il riferimento all'array non voglio che cambi una volta inizializzato dal costruttore
    private final Boolean postazioni[];
    private final int tesi_pc;

    public Laboratorio() {
        postazioni = new Boolean[LAB_SIZE];
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni[i] = AVAILABLE;
        }
        // la postazione da tesisti è fissa per un'esecuzione, generata casualmente
        tesi_pc = ThreadLocalRandom.current().nextInt(LAB_SIZE);
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
            postazioni[i] = NOT_AVAILABLE;
        }
    }

    public void book(Tesista t) {
        postazioni[tesi_pc] = NOT_AVAILABLE;
    }

    /**
     * @effects     Occupa la postazione di indice "index"
     * @param s     Studente
     * @param index indice del pc che vuole occupare lo studente
     */
    public void book(Studente s, int index) {
        postazioni[index] = NOT_AVAILABLE;
    }

/* Rilasci delle postazioni */

    /** valgono per tutti i leave()
     * @effects 	liberano i/il pc precedentemente occupato
     * @param p/t/s oggetto Utente chiamante
     */
    public void leave(Professore p) {
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni[i] = AVAILABLE;
        }
    }

    public void leave(Tesista t) {
        postazioni[tesi_pc] = AVAILABLE;
    }

    /**
     * @param s		oggetto Studente chiamante
     * @param index indice del pc occupato dallo studente
     */
    public void leave(Studente s, int index) {
        postazioni[index] = AVAILABLE;
    }

    /**
     * @return  indice della prima postazione libera, se ce ne sono
     *          altrimenti -1
     */
    public int findAvailable() {
        for(int i = 0; i < LAB_SIZE; i++) {
            if(postazioni[i] == AVAILABLE)
                return i;
        }
        return -1;
    }

    /**
     * @return l'indice del pc per i tesisti
     */
    public int getTesi_pcIndex() {
        return tesi_pc;
    }

    /**
     * @return  TRUE se il pc da tesisti è occupato
     *          else FALSE (ovvero se è libero)
     */
    public boolean isTesiPCBusy() {
        if(postazioni[tesi_pc] == NOT_AVAILABLE)    return true;
        else    return false;
    }

    /**
     * @return  il numero di postazioni libere
     */
    public int computersAvailable() {
        int cnt = 0;
        for(int i = 0; i < LAB_SIZE; i++) {
            if(postazioni[i] == AVAILABLE)
                cnt++;
        }
        return cnt;
    }

    /**
     * @return  TRUE iff tutte le postazioni sono libere
     */
    public boolean isEmpty() {
        if(computersAvailable() == LAB_SIZE)
            return true;
        else
            return false;
    }
}
