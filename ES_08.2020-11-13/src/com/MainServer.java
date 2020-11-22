package com;

/*
Scrivere un programma JAVA che implementa un server TCP che apre una listening socket su una porta e
resta in attesa di richieste di connessione.

    Quando arriva una richiesta di connessione, il server accetta la connessione, trasferisce
    al client un messaggio ("HelloClient") e poi chiude la connessione.
    Usare multiplexed NIO (canali non bloccanti e il selettore, e ovviamente i buffer di tipo
    ByteBuffer).
    Per il client potete usare un client telnet.
 */

public class MainServer {
    private static final int SERV_PORT = 60029;

    public static void main(String[] args) {
        ServerHello server = new ServerHello(SERV_PORT);
        server.start();
    }
}
