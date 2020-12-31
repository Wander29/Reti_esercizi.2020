import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Si progetti un’applicazione Client/Server per la gestione delle registrazioni ad un evento. Il server espone operazioni per:
 * - registrare un utente all'evento (l'operazione richiede che il client fornisca il nome dell'utente)
 * - ottenere la lista degli utenti registrati
 * Il client registra un insieme di utenti, richiede la lista degli utenti registrati e termina.
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class MainClassServer {
    private static final int PORT_DEFAULT = 5000;

    /**
     * usage
     */
    private static void usage(){
        System.out.println("java MainClassServer [port]");
    }

    public static void main(String[] args){
        int port = PORT_DEFAULT;
        if (args.length > 0 && args.length!=1){
            usage();
            return;
        }
        if (args.length == 1)
            port = Integer.parseInt(args[0]);
        try {
            EventManager eventManager = new EventManager();
            // esporta l'oggetto
            EventManagerInterface stub = (EventManagerInterface) UnicastRemoteObject.exportObject(eventManager, port);

            // crea il registro
            LocateRegistry.createRegistry(port);
            Registry register = LocateRegistry.getRegistry(port);

            // binding
            register.rebind("EVENT_MANAGER", stub);
            System.out.println("Server è pronto");
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
