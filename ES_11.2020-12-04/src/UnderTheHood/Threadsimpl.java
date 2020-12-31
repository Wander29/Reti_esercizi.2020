package UnderTheHood;

import java.rmi.*;
import java.rmi.server.RemoteObject;

public class Threadsimpl extends RemoteObject implements ThreadsInt {

    public Threadsimpl() throws RemoteException { super(); }

    public void methodOne() throws RemoteException {
        long TimeOne = System.currentTimeMillis();

        for(int index=0;index<25;index++) {
            System.out.println("Method ONE executing");
// Inserito un ritardo di circa mezzo secondo
            do{ } while ((TimeOne+500)>System.currentTimeMillis());
            TimeOne = System.currentTimeMillis();
        }
    }

    public void methodTwo() throws RemoteException {
        long TimeTwo = System.currentTimeMillis();

        for(int index=0;index<25;index++) {
            System.out.println("Method TWO executing");
// Inserito un ritardo di circa mezzo secondo
            do{ }while ((TimeTwo+500)>System.currentTimeMillis());
            TimeTwo = System.currentTimeMillis();
        }
    }
}

