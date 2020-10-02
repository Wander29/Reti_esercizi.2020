package es01;

public class Reader implements Runnable {

    private Counter cnt;
    private final int index;

    public Reader(Counter c, int index) {
        this.index = index;
        this.cnt = c;
    }

    public void run() {
        System.out.println("COUNTER: [" + cnt.get() + "]");
    }
}
