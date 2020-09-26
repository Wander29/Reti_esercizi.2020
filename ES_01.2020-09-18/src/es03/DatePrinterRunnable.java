package es03;

import java.util.Calendar;
import java.util.Date;

import static java.lang.Thread.sleep;

public class DatePrinterRunnable implements java.lang.Runnable {
    public DatePrinterRunnable() {}

    @Override
    public void run() {
        while(true) {
            Calendar tempo = Calendar.getInstance();
            Date data = tempo.getTime();

            System.out.println("THREAD [" + Thread.currentThread().getName() + "] : Data e ora [ " +
                    data.toString() + "]");
            try {
                sleep(2000);
            } catch(InterruptedException e) {
                System.out.println("daie aoh");
            }
        }
    }
}
