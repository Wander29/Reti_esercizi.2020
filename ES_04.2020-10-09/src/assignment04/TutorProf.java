package assignment04;

/**
 * @author		LUDOVICO VENTURI 578033
 * @date		2020/10/23
 * @versione	1.1
 */

/*
    Classe Wrapper dei metodi di Tutor2, consentirà solo alla tipologia di Utente "Professori" di effettuare
        prenotazioni/rilasci (evitando quindi di passare «this» ai metodi)
 */

public class TutorProf {
    private final Tutor2 tutor;

    public TutorProf(Tutor2 tutorlab) {
        tutor = tutorlab;
    }

    public void occupy() throws InterruptedException {
        tutor.bookProf();
    }

    public void release() {
        tutor.leaveProf();
    }
}
