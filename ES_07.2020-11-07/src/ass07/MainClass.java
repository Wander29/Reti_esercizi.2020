package ass07;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/19
 * @version1    1.0
*/

/*************************************************************
 * Struttura del codice
 * - parte il Server HTTP
 * -> si mette in ascolto di eventuali client, per poi stabilire una connessione
 * -> ogni comunicazione con 1 cliente viene gestita da 1 ClientHandler (thread)
 ************************************************************/

public class MainClass {
    final static int PORT = 9999;

    public static void main(String args[]) {

        ServerHTTP server = new ServerHTTP(PORT);

        server.start();

        System.out.println("MAIN - Correct exiting");
    }
}
