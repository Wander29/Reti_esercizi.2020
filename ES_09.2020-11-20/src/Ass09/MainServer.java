package Ass09;

/**
 * INPUT
 * [#]  accetta un argomento da linea di comando: la porta, che è quella su cui è
 *      attivo il server.
 * [#]  Se uno qualunque degli argomenti è scorretto, stampa un
 *      messaggio di errore del tipo ERR -arg x, dove x è il numero dell'argomento.
 *
 *   Lo scopo di questo assignment è quello di implementare un server PING ed
 * un corrispondente client PING che consenta al client di misurare il suo RTT
 * verso il server.
 *
 * il Client  utilizza una comunicazione UDP per comunicare con il server ed invia 10
 * messaggi al server, con il seguente formato:
 * PING seqno timestamp
 */

public class MainServer {
    public static void main(String args[]) {

        ServerPing server = new ServerPing(9999);
        server.start();
    }
}
