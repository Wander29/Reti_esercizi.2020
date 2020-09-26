import java.util.Calendar;

/**
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class DatePrinterRunnable implements Runnable {

    public static void main(String[] args) {
        Thread dataPrinterTh = new Thread(new DatePrinterRunnable());
        /* oppure alternativamente
            DataPrinterRunnable dataPrinterRunn = new DataPrinterRunnable();
            Thread dataPrinterTh = new Thread(dataPrinterRunn);
         */
        // manda in esecuzione il thread
        dataPrinterTh.start();
        // stampa il nome del thread corrente
        System.out.println(Thread.currentThread().getName());
    }

    @Override
    public void run() {
        while (true) {
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
    }
}
