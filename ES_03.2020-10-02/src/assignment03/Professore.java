package assignment03;

public class Professore extends Utente implements Runnable {
    public Professore(Tutor t) {
        this.k = (int) (Math.random() * this.MAX_EXEC);
        this.tutor = t;
    }

    public void run() {
        System.out.println("[PROF] Runno! " + k);
        for(int i = 0; i < k; i++) {
            /* accesso al laboratorio, chiamata eventualemente bloccante */
            if(!tutor.book(this)) {
                System.out.println("[PROF] Book error");
                return;
            }

            /* utilizzo del laboratorio */
            try {
                System.out.println("[PROF] Scanzateve, ora comando io");
                Thread.sleep((int) Math.random() * 2000);
            } catch (InterruptedException e) {
                System.out.println("[PROF] Sleep interrotta");
                return;
            }

            /* rilascio del laboratorio e attesa del prossimo utilizzo */
            if(!tutor.leave(this)) {
                System.out.println("[PROF] Leave error");
                return;
            }
            try {
                Thread.sleep((int) Math.random() * 3000);
            } catch (InterruptedException e) {
                System.out.println("[PROF] Sleep interrotta");
                return;
            }
        }
    }
}
