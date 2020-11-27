import java.net.UnknownHostException;

/**
 * Definire un server WelcomeServer, che
 *
 * - invia su un gruppo di multicast (welcomegroup ad esempio con indirizzo IP 239.255.1.3), ad intervalli
 *   regolari, un messaggio di «welcome».
 * - attende tra un invio ed il successivo un intervallo di tempo simulato
 *   mediante il metodo sleep().
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class MainClassServer {
    /**
     * indirizzo del gruppo di multicast
     */
    final static String DEFAULT_WELCOME_GROUP = "239.255.1.3";
    /**
     * porta associata all'indirizzo di multicast
     */
    final static int DEFAULT_WELCOME_PORT = 30000;


    public static void main(String[] args) {

        try {
            // crea e avvia il WelcomeServer
            WelcomeServer server = new WelcomeServer(DEFAULT_WELCOME_GROUP, DEFAULT_WELCOME_PORT);
            server.start();
        }
        catch (UnknownHostException e){             // l'indirizzo passato non è valido
            System.err.println("L'indirizzo immesso non e' valido");
        }
        catch(IllegalArgumentException e){          // l'indirizzo passato non è un indirizzo multicast
            System.err.println("L'indirizzo immesso non e' un indirizzo multicast");
        }
    }


}
