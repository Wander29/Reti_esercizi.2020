/**
 * Professore modella un professore che vuole svolgere attività didattiche per le quali è richiesto
 * l'utilizzo dell'intero laboratorio
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class Professore extends Utente {

    /**
     *
     * @param m identificativo del professore
     * @param t tutor
     */
    public Professore(int m, Tutor t) {
        super(m, t);
    }

    @Override
    void richiestaAccesso() {
        System.out.printf("Professore %d ha richiesto il laboratorio\n", this.getMatricola());
        tutor.professoreRichiedeLab(this);
        System.out.printf("Professore %d ha ottenuto il laboratorio\n", this.getMatricola());
    }

    @Override
    void rilascio() {
        System.out.printf("Professore %d rilascia il laboratorio\n", this.getMatricola());
        tutor.professoreRilasciaLab(this);
    }
}
