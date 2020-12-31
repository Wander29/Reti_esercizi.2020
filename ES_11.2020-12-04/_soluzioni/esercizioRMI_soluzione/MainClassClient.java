import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class MainClassClient {
    private static final int PORT_DEFAULT = 5000;

    /**
     * usage
     */
    private static void usage(){
        System.out.println("java MainClassClient [port]");
    }

    /**
     * registra l'utente attraverso remoteEventManager
     *
     * @param name username da registrare
     * @param remoteEventManager remote reference (EventManager)
     */
    private static void registerUser(String name, EventManagerInterface remoteEventManager){
        System.out.println("Richiedo la registrazione di " + name);
        try {
            if (remoteEventManager.registerUser(name)) {
                System.out.printf("L'utente \"%s\" è stato registrato con successo\n", name);
            }
            else {
                System.out.printf("L'utente \"%s\" era già registrato\n", name);
            }
        }
        catch (RemoteException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        int port = PORT_DEFAULT;
        if (args.length > 0 && args.length!=1){
            usage();
            return;
        }
        try (BufferedReader in  = new BufferedReader(new InputStreamReader(System.in))){

            // ottiene una reference per il registro
            Registry r = LocateRegistry.getRegistry(port);
            // ottiene una reference all'event manager
            EventManagerInterface remoteEventManager = (EventManagerInterface) r.lookup("EVENT_MANAGER");

            System.out.println("Inserisci il nome da registrare all'evento");
            // prende input (linea di comando) il nome dell'utente
            String username = in.readLine();

            // registra l'utente all'evento
            registerUser(username, remoteEventManager);
            System.out.println();

            System.out.println("Lista degli utenti registrati");
            // richiede la lista degli utenti registrati
            System.out.println(remoteEventManager.getListUsers());

        } catch(IOException | NotBoundException e){
            e.printStackTrace();
        }

    }
}
