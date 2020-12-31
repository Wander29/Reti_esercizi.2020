package UnderTheHood;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Threadserver {
    public Threadserver(int porta) {
        try {
            LocateRegistry.createRegistry(porta);
            Registry r=LocateRegistry.getRegistry(porta);
            System.out.println("Registro Reperito");
            Threadsimpl c = new Threadsimpl();
            ThreadsInt stub =(ThreadsInt)
                    UnicastRemoteObject.exportObject(c, 0);
            r.rebind("Threads", stub); }
        catch (Exception e) {
            System.out.println("Server Error: " + e); }
    }

    public static void main(String args[]) {
        new Threadserver(9999);
    }}
