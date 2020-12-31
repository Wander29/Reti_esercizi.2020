package PassaggioOggettoDaSerializzare;

import java.rmi.*;
import java.rmi.registry.*;
public class CircleServer {
    public static void main(String args[]) throws Exception {
        Circle circle = new CircleImpl(10, 20, 30);
        LocateRegistry.createRegistry(9999);
        Registry r=LocateRegistry.getRegistry(9999);
        r.rebind(Circle.SERVICE_NAME, circle);
        System.out.println("Circle bound in registry");
    }}
