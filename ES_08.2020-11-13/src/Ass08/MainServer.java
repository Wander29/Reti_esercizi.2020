package Ass08;

/**
 * scrivere un programma echo server usando :
 * - la libreria java NIO e, in particolare
 * - il Selector e
 * - canali in modalità non bloccante,
 *
 * e un programma echo client, usando NIO (va bene anche con modalità bloccante).
 *
 *     Il server accetta richieste di connessioni dai client, riceve messaggi inviati dai client e li
 *     rispedisce (eventualmente aggiungendo "echoed by server" al messaggio ricevuto).
 *
 *     Il client legge il messaggio da inviare da console, lo invia al server e visualizza quanto
 *     ricevuto dal server.
 */

public class MainServer {
    public static void main(String args[]) {
        int port = 9999;

        ServerEcho server = new ServerEcho(port);
        server.start();
    }
}
