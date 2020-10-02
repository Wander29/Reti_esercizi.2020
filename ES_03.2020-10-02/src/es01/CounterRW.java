package es01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Counter modella il contatore
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class CounterRW extends Counter {

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private Lock read = rwLock.readLock();
    private Lock write = rwLock.writeLock();

    /**
     * incrementa di 1 unità il valore del contatore
     */
    public void increment(){
        write.lock();
        this.contatore++;
        write.unlock();
    }

    /**
     *
     * @link Counter#contatore
     */
    public int get(){
        int tmp;

        read.lock();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrotta");
        }
        tmp = this.contatore;

        read.unlock();
        return tmp;
    }
}
