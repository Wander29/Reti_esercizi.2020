import java.net.UnknownHostException;

/**
 * Definire un client WelcomeClient che si unisce a welcomegroup, riceve un messaggio di welcome, quindi termina.
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class MainClassClient {
    /**
     * indirizzo di broadcast
     */
    final static String DEFAULT_WELCOME_GROUP = "239.255.1.3";
    /**
     * porta associata all'indirizzo di multicast
     */
    final static int DEFAULT_WELCOME_PORT = 30000;


    public static void main(String[] args){
        try {
            // crea e avvia il WelcomeClient
            WelcomeClient client = new WelcomeClient(DEFAULT_WELCOME_GROUP, DEFAULT_WELCOME_PORT);
            client.start();
        }
        catch (UnknownHostException e){             // l'indirizzo passato non è valido
            System.err.println("L'indirizzo immesso non e' valido");
        }
        catch(IllegalArgumentException e){          // l'indirizzo passato non è un indirizzo multicast
            System.err.println("L'indirizzo immesso non e' un indirizzo multicast");
        }
    }
}
