package RMI_callback;

/*
Un server gestisce le quotazioni di borsa di un titolo azionario. Ogni volta che si
verifica una variazione del valore del titolo, vengono avvertiti tutti i client che si
sono registrati per quell'evento.
Il server definisce un oggetto remoto che fornisce metodi per
●
 consentire al client di registrare/cancellare una callback
Il client vuole essere informato quando si verifica una variazione
•
 espone un metodo per la notifica
•
 registra una callback presso il server.
•
 aspetta per un certo intervallo di tempo durante cui riceve le variazioni di
quotazione
•
 alla fine cancella la registrazione della propria callback presso il server

 */

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

public class MainClient {
        public static void main(String args[]) {
            try {
                System.out.println("Cerco il Server");
                Registry registry = LocateRegistry.getRegistry(5000);

                String name = "Server";
                ServerInterface server = (ServerInterface) registry.lookup(name);

                /* si registra per la callback */
                System.out.println("Registering for callback");
                NotifyEventInterface callbackObj = new NotifyEventImpl();

                NotifyEventInterface stub = (NotifyEventInterface)
                        UnicastRemoteObject.exportObject(callbackObj, 0);

                server.registerForCallback(stub);
                // attende gli eventi generati dal server per
// un certo intervallo di tempo;
                Thread.sleep (20000);
                /* cancella la registrazione per la callback */
                System.out.println("Unregistering for callback");
                server.unregisterForCallback(stub);
            }
            catch (Exception e) { System.err.println("Client exception:"+ e.getMessage()); }
        }
}
