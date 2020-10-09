import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLockCounter modella il contatore che utilizza una ReentrantLock per la mutua esclusione
 * @author Samuel Fabrizi
 * @version 1.0
 */

public class ReentrantLockCounter extends Counter {
    private final Lock reentrantLock = new ReentrantLock();

    @Override
    public void increment(){
        reentrantLock.lock();
        super.increment();
        reentrantLock.unlock();
    }

    @Override
    public int get(){
        reentrantLock.lock();
        int current_c = super.get();
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        reentrantLock.unlock();
        return current_c;
    }
}
