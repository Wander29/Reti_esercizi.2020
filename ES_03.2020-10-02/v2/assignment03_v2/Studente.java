package assignment03_v2;

public class Studente extends Utente implements Runnable {

    private final int k; /* numero accessi al laboratorio */

    public Studente() {
        this.setPriority(STUD_PRIORITY);
        this.k = (int) Math.random() * MAX_EXEC;
    }

    @Override
    public void joinLab() {

    }

    @Override
    public void exitLab() {

    }

    @Override
    public void run() {
        for(int i = 0; i < k; i++) {
            /* accesso al laboratorio, chiamata eventualemente bloccante */
            joinLab();

            /* utilizzo del laboratorio */
            try {
                Thread.sleep((int) Math.random() * 2000);
            } catch (InterruptedException e) {
                System.out.println("[STUDENTE] Sleep interrotta");
                return;
            }

            /* rilascio del laboratorio e attesa del prossimo utilizzo */
            exitLab();

            try {
                Thread.sleep((int) Math.random() * 3000);
            } catch (InterruptedException e) {
                System.out.println("[STUDENTE] Sleep interrotta");
                return;
            }
        }
    }
}
