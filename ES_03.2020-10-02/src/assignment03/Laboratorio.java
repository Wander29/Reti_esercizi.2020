package assignment03;

import java.util.concurrent.ThreadLocalRandom;
/*
    Il Laboratorio implementa le gestione effettiva delle postazioni, senza pensare alla concorrenza
 */

public class Laboratorio {
    /** per ogni metodo:
     * @REQUIRES esecuzione in mutua esclusione, garantita dal chiamante
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
        tesi_pc = ThreadLocalRandom.current().nextInt(LAB_SIZE);
    }

    public void book(Professore p) {
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni[i] = NOT_AVAILABLE;
        }
    }

    public void book(Tesista t) {
        postazioni[tesi_pc] = NOT_AVAILABLE;
    }

    public void book(Studente s, int index) {
        postazioni[index] = NOT_AVAILABLE;
    }

    public void leave(Professore p) {
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni[i] = AVAILABLE;
        }
    }

    public void leave(Tesista t) {
        postazioni[tesi_pc] = AVAILABLE;
    }

    public void leave(Studente s, int index) {
        postazioni[index] = AVAILABLE;
    }

    public int findAvailable() {
        for(int i = 0; i < LAB_SIZE; i++) {
            if(postazioni[i] == AVAILABLE)
                return i;
        }
        return -1;
    }

    public int getTesi_pcIndex() {
        return tesi_pc;
    }

    public boolean isEmpty() {
        if(computersAvailable() == LAB_SIZE)
            return true;
        else
            return false;
    }

    public int computersAvailable() {
        int cnt = 0;
        for(int i = 0; i < LAB_SIZE; i++) {
            if(postazioni[i] == AVAILABLE)
                cnt++;
        }
        return cnt;
    }

    public boolean isTesiPCBusy() {
        if(postazioni[tesi_pc] == NOT_AVAILABLE)    return true;
        else    return false;
    }
}
