package es01;

public class Writer implements Runnable {

    private Counter cnt;
    private final int index;

    public Writer (Counter c, int index) {
        this.index = index;
        this.cnt = c;
    }

    public void run() {
        this.cnt.increment();
    }
}