package assignment03;

import java.util.ArrayList;

/*
    Il Laboratorio implementa le gestione effettiva delle postazioni, senza pensare alla concorrenza
 */

public class Laboratorio {
    /** per ogni metodo:
     * @REQUIRES esecuzione in mutua esclusione, garantita dal chiamante
     */

    private final int LAB_SIZE = 20;

    private ArrayList<Utente> postazioni;
    private final int tesi_pc;

    public Laboratorio() {
        postazioni = new ArrayList<>(20);
        postazioni.ensureCapacity(20);
        tesi_pc = (int) (Math.random() * LAB_SIZE);
    }

    public void book(Professore p) {
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni.add(i, (Utente) p);
        }
    }

    public void book(Tesista t) {
        postazioni.add(tesi_pc, (Utente) t);
    }

    /* potrebbe anche occupare il pc_tesi */
    public void book(Studente s) {
        postazioni.add((Utente) s);
// DEGBUG
        if(postazioni.indexOf(s) == tesi_pc) {
            System.out.println("STUDENTE OCCUPA PC TESI!");
        }
    }

    public void leave(Professore p) {
        for(int i = 0; i < LAB_SIZE; i++) {
            postazioni.remove(i);
        }
        assert(remainingSize() == LAB_SIZE);
    }

    public void leave(Tesista t) {
        postazioni.remove(tesi_pc);
    }

    public void leave(Studente s) {
        postazioni.remove(s);
    }

    public int remainingSize() {
        return LAB_SIZE - postazioni.size();
    }

    public boolean isEmpty() {
        if(0 == postazioni.size())
            return true;
        else
            return false;
    }

    public boolean isFull() {
        if(LAB_SIZE == postazioni.size())
            return true;
        else
            return false;
    }

    public boolean isTesiPCBusy() {
        if(postazioni.get(tesi_pc) != null)
            return true;
        else
            return false;
    }
}
