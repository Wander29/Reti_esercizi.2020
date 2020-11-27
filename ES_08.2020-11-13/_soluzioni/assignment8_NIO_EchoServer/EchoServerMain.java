/**
 * Scrivere un programma echo server usando la libreria java NIO e, in particolare, il Selector e canali in modalità non bloccante, e un programma echo client, usando NIO (va bene anche con modalità bloccante).
 * Il server accetta richieste di connessioni dai client, riceve messaggi inviati dai client e li rispedisce (eventualmente aggiungendo "echoed by server" al messaggio ricevuto).
 * Il client legge il messaggio da inviare da console, lo invia al server e visualizza quanto ricevuto dal server.
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
class EchoServerMain {
    final static int DEFAULT_PORT = 9999;

    public static void main(String[] args){
        int myPort = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                myPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Fornire il numero di porta come intero");
                System.exit(-1);
            }
        }
        // crea e avvia il server
        EchoServer server = new EchoServer(myPort);
        server.start();
    }
}
