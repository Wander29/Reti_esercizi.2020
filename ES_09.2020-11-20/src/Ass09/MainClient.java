package Ass09;

/**
 * INPUT
 *  * [#]  accetta due argomenti da linea di comando: nome
 *  *      e porta del server
 *  * [#]  Se uno o più argomenti risultano scorretti, il client termina, dopo aver stampato
 *  *      un messaggio di errore del tipo ERR -arg x, dove x è il numero
 *  *      dell'argomento.
 */
public class MainClient {
    public static void main(String args[]) {
        ClientPing client = new ClientPing();
        client.start();
    }
}
