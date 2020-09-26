package es03;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String args[]) {
        DatePrinterRunnable dpr = new DatePrinterRunnable();
        Thread dpt = new Thread(dpr);

        dpt.start();

        try {
            sleep(2000);
        } catch(InterruptedException e) {
            System.out.println("daie aoh");
        }
        System.out.println("THREAD [" + Thread.currentThread().getName() + "] in esecuzione /es03");
    }
}
