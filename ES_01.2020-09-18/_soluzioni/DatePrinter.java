import java.util.Calendar;

/**
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class DatePrinter {

    public static void main(String[] args){
        while(true) {
            // stampa data e ora corrente
            System.out.println(Calendar.getInstance().getTime());
            // stampa il nome del thread corrente
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrotta");
                return;
            }
        }
        // La seguente istruzione non viene mai eseguita a causa del ciclo infinito
        // System.out.println(Thread.currentThread().getName());
    }
}
