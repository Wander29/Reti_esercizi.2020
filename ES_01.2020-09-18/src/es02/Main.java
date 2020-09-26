package es02;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String args[]) {
        DatePrinterThread dpt = new DatePrinterThread();
        dpt.start();

        try {
            sleep(5000);
        } catch(InterruptedException e) {
            System.out.println("daie aoh");
        }
        dpt.interrupt();
        System.out.println("THREAD [" + Thread.currentThread().getName() + "]");
    }
}
