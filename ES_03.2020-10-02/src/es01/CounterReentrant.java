package es01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Counter modella il contatore
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class CounterReentrant extends Counter {

    private final Lock lock = new ReentrantLock();
    /**
     * rappresenta il contatore
     */
    private int contatore = 0;

    /**
     * incrementa di 1 unità il valore del contatore
     */
    public void increment(){
        lock.lock();
        contatore++;
        lock.unlock();
    }

    /**
     *
     * @link Counter#contatore
     */
    public int get(){
        int tmp;

        lock.lock();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrotta");
        }
        tmp = contatore;
        lock.unlock();

        return tmp;
    }
}
