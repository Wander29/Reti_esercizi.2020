package assignment06;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private ContoCorrente cc;
    private TreeMap<String, AtomicInteger> freq;

    public Worker(TreeMap tm, ContoCorrente c) {
        this.cc = c;
        this.freq = tm;
    }

    public void run() {
        for(Movimento m : cc.getMovimenti()) {
            freq.get(m.causale).incrementAndGet();
        }
    }
}
