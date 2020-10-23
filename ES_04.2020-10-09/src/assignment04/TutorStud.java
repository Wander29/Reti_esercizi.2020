package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

/*
    Classe Wrapper dei metodi di Tutor2, consentirà solo alla tipologia di Utente "Studenti" di effettuare
        prenotazioni/rilasci (evitando quindi di passare «this» ai metodi)
 */

public class TutorStud {
    private final Tutor2 tutor;

    public TutorStud(Tutor2 tutorlab) {
        tutor = tutorlab;
    }

    public int occupy() throws InterruptedException {
        return tutor.bookStud();
    }

    public void release(int ind) {
        tutor.leavePC(ind);
    }
}