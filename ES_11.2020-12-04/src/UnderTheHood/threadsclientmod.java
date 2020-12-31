package UnderTheHood;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.lang.*;
public class threadsclientmod {
    public static void main(String[] args) {
        try {
            Registry r = LocateRegistry.getRegistry(9999);
            ThreadsInt c = (ThreadsInt) r.lookup("Threads");
            OneThread t1 = new OneThread(c);
            t1.start();
            TwoThread t2 = new TwoThread(c);
            t2.start();

            t1.join();
            t2.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
