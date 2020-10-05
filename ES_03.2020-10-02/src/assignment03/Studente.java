package assignment03;

public class Studente extends Utente implements Runnable {

    public Studente(Tutor l) {
        this.k = (int) Math.random() * MAX_EXEC;
        this.tutor = l;
    }

    public void run() {
        for(int i = 0; i < k; i++) {
            /* accesso al laboratorio, chiamata eventualemente bloccante */
            if(!tutor.book(this)) {
                System.out.println("[STUDENTE] Book error");
                return;
            }

            /* utilizzo del laboratorio */
            try {
                System.out.println("[STUDENTE] Sto per usare un pc!");
                Thread.sleep((int) Math.random() * 2000);
            } catch (InterruptedException e) {
                System.out.println("[STUDENTE] Sleep interrotta");
                return;
            }

            /* rilascio del laboratorio e attesa del prossimo utilizzo */
            if(!tutor.leave(this)) {
                System.out.println("[STUDENTE] Leave error");
                return;
            }
            try {
                Thread.sleep((int) Math.random() * 3000);
            } catch (InterruptedException e) {
                System.out.println("[STUDENTE] Sleep interrotta");
                return;
            }
        }
    }
}
