import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * EventManager modella il gestore dell'evento. Si occupa della registrazione degli utenti
 *
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class EventManager extends RemoteServer implements EventManagerInterface {

    private final LinkedList<String> users;

    /**
     *
     * @throws RemoteException se si verificano durante l'esecuzione della chiamata remota
     */
    public EventManager() throws RemoteException {
        //passo al costruttore un oggetto di tipo "Comparator_id" affinché la struttura dati sia ordinata per identificativo
        this.users = new LinkedList<String>();
    }


    public synchronized boolean registerUser(String username) throws IllegalArgumentException, RemoteException {
        System.out.println("Un utente ha richiesto la registrazione all'evento");
        if (username == null)
            throw new IllegalArgumentException("Nome non valido");
        if (!this.users.contains(username)){
            this.users.add(username);
            return true;                                // l'utente e' stato registrato correttamente
        }
        else {
            return false;                               // l'utente era già registrato
        }
    }

    public synchronized String getListUsers() throws RemoteException{
        System.out.println("Un utente ha richiesto la lista degli utenti registrati");
        StringBuilder listUsers = new StringBuilder();
        for (String user: users)                        // crea la lista degli utenti registrati al servizio
            listUsers.append(user).append("\n");
        return listUsers.toString();
    }

}
