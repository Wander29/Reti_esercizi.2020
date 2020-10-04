package assignment03;

public class Professore implements Runnable {
    private final int k; /* numero esecuzioni */
    private final int MAX_EXEC = 20;
    private final Laboratorio lab;

    public Professore(Laboratorio l) {
        this.k = (int) Math.random() * MAX_EXEC;
        this.lab = l;
    }

    public void run() {
        for(int i = 0; i < k; i++) {
            /* accesso al laboratorio, chiamata eventualemente bloccante */
            if(!lab.bookProf()) {
                System.out.println("[PROF] Book error");
                return;
            }

            /* utilizzo del laboratorio */
            try {
                Thread.sleep((int) Math.random() * 2000);
            } catch (InterruptedException e) {
                System.out.println("[PROF] Sleep interrotta");
                return;
            }

            /* rilascio del laboratorio e attesa del prossimo utilizzo */
            if(!lab.leaveProf()) {
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
