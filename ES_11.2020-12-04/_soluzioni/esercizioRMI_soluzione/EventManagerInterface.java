import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * EventManagerInterface rappresenta l'interfaccia offerta al client
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public interface EventManagerInterface extends Remote {

    /**
     *
     * @param username nome dell'utente da registrare
     * @return      true se l'utente è stato correttamente registrato
     *              false se l'utente era già registrato
     * @throws RemoteException se si verificano durante l'esecuzione della chiamata remota
     * @throws IllegalArgumentException se l'argomento passato risulta invalido
     */
    boolean registerUser(String username) throws RemoteException, IllegalArgumentException;

    /**
     *
     * @return lista degli utenti registrati al servizio
     * @throws RemoteException se si verificano durante l'esecuzione della chiamata remota
     */
    String getListUsers() throws RemoteException;
}
