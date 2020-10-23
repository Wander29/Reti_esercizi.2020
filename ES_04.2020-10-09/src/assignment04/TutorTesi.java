package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

/*
    Classe Wrapper dei metodi di Tutor2, consentirà solo alla tipologia di Utente "Tesisti" di effettuare
        prenotazioni/rilasci (evitando quindi di passare «this» ai metodi)
 */

public class TutorTesi {
    private final Tutor2 tutor;

    public TutorTesi(Tutor2 tutorlab) {
        tutor = tutorlab;
    }

    public void occupy(long mat, int ind) throws InterruptedException {
        tutor.bookTesi(mat, ind);
    }

    public void release(int ind) {
        tutor.leavePC(ind);
    }

    public int identifyPc() {
        return this.tutor.identifyPcTesista();
    }
}