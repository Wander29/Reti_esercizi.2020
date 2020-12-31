package RMI_callback;

import java.rmi.server.*; import java.rmi.registry.*;
public class MainServer {
    public static void main(String[] args) {
        try{
            /*registrazione presso il registry */
            ServerImpl server = new ServerImpl( );
            //ServerInterface stub = (ServerInterface)
             //       UnicastRemoteObject.exportObject (server,39000);

            String name = "Server";
            LocateRegistry.createRegistry(5000);
            Registry registry = LocateRegistry.getRegistry(5000);
            registry.bind(name, server);

            while (true) {
                int val = (int) (Math.random( )*1000);
                System.out.println("nuovo update " + val);
                server.update(val);
                Thread.sleep(1500);}
        } catch (Exception e) { System.out.println("Eccezione" +e);}}}

