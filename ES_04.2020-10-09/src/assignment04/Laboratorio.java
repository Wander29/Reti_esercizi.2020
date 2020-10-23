package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

import java.util.ArrayList;
/*
    Il Laboratorio implementa le gestione effettiva delle postazioni, senza pensare alla concorrenza
 */

public class Laboratorio extends StrutturaLaboratorio {
    /** NON è una classe Thread-Safe
     * per ogni metodo:
     * @REQUIRES esecuzione in mutua esclusione, che DEVE essere garantita dal chiamante
     */

    // costanti per favorire la human readability
    private final Boolean AVAILABLE = Boolean.TRUE;
    private final Boolean NOT_AVAILABLE = Boolean.FALSE;

    // postazione[i] = TRUE se la postazione è libera, FALSE altrimenti
    // l'array è final poichè il riferimento all'array non voglio che cambi una volta inizializzato dal costruttore
    private final ArrayList<Boolean> postazioni;

    public Laboratorio() {
        this.postazioni = new ArrayList<>(LAB_SIZE);

        for(int i = 0; i < LAB_SIZE; i++) {
            this.postazioni.add(Boolean.TRUE);
        }
    }

/** PRENOTAZIONI

 */
    /** valgono per tutti i book()
     * @effects		occupano i/il pc richiesto
     */
    public void bookProf() {
        for(int i = 0; i < LAB_SIZE; i++) {
            this.postazioni.set(i, NOT_AVAILABLE);
        }
    }

    /**
     * occupa uno specifico PC
     * @param index indice del pc da occupare
     */
    public void bookPC(int index) {
        this.postazioni.set(index, NOT_AVAILABLE);
    }

/* Rilasci delle postazioni */

    /** valgono per tutti i leave()
     * @effects 	liberano i/il pc precedentemente occupato
     */
    public void leaveProf() {
        for(int i = 0; i < LAB_SIZE; i++) {
            this.postazioni.set(i, AVAILABLE);
        }
    }

    /**
     * @param index indice del pc occupato dall'utente
     */
    public void leavePC(int index) {
        this.postazioni.set(index, AVAILABLE);
    }

    /**
     * @return  TRUE iff tutte le postazioni sono libere
     */
    public boolean isEmpty() {
        for(Boolean b : this.postazioni) {
            if(b == NOT_AVAILABLE) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param index     indice del pc da controllare
     * @return          true iff il pc di indice index è libero
     */
    public boolean isPcFree(int index) {
        return this.postazioni.get(index);
    }
}
