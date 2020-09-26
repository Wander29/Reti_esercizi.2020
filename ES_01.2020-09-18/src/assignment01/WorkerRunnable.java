package assignment01;

/**
	@AUTHOR Ludovico Venturi
	@DATE   2020/09/18
*/

/*
    questa classe è adibita allo svolgimento del task del calcolo di PI
    implementa Runnable, pertanto per essere eseguita da uno specifico Thread
    un'istanza di WorkerRunnable andrà passata al costruttore del Thread, per poi venire attivato
 */
public class WorkerRunnable implements java.lang.Runnable {
    private double accuracy; /* accuratezza del calcolo, espressa come |PI - computatedPI| */

    public WorkerRunnable() {
        accuracy = 0;
    };

    public WorkerRunnable(double acc) {
        this.accuracy = acc;
    }

    public void setAccuracy(double acc) {
        if(acc > 0) { this.accuracy = acc; }
    }

    public void run() {
        /*
        Il thread T effettua un ciclo infinito per il calcolo di π usando la serie di Gregory-Leibniz
            (π = 4/1 – 4/3 + 4/5 - 4/7 + 4/9 - 4/11 ...).
         */
        double denominatore = 1;
        double current = 0;

        while(true) {
            current += 4/denominatore;
            denominatore += 2;
            current -= 4/denominatore;
            denominatore += 2;

        /* controllo interruzioni */
            if(Thread.interrupted() == true) {
                System.out.println("PI value computated: [" + current + "], not satisfying Accuracy");
                return;
            }

        /* controllo accuracy */
            if(Math.abs((current - Math.PI)) <= accuracy) {
                System.out.println("PI value computated: [" + current + "], satisfying Accuracy, exiting");
                return;
            }
        }
    }
}
