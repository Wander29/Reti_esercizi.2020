import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLockCounter modella il contatore che utilizza una ReadWriteLock per la mutua esclusione
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class ReadWriteLockCounter extends Counter {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    @Override
    public void increment(){
        writeLock.lock();
        super.increment();
        writeLock.unlock();
    }

    @Override
    public int get(){
        int current_c;
        readLock.lock();
        current_c = super.get();

        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        readLock.unlock();
        return current_c;
    }
}

