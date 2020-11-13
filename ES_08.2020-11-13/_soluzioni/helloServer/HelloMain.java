/**
 * Scrivere un programma JAVA che implementa un server TCP che
 * apre una listening socket su una porta e resta in attesa di richieste
 * di connessione.
 * Quando arriva una richiesta di connessione, il server accetta la
 * connessione, trasferisce al client un messaggio ("HelloClient") e poi
 * chiude la connessione.
 * Usare multiplexed NIO (canali non bloccanti e il selettore, e
 * ovviamente i buffer di tipo ByteBuffer).
 * - Per il client potete usare un client telnet.
 * - Due opzioni possibili (per l’esercizio)
 *
 * 1. Opzione più semplice:
 * ● come primo esercizio potete sviluppare un programma in cui
 * quando la serverSocketChannel ha connessioni da accettare
 * (key.isAcceptable() è vera) il server scrive subito sulla socketChannel
 * restituita dall'operazione di accept() e chiude la connessione.
 * 2. Opzione più completa (ma un po' più complicata - vedi esempio
 * IntGenServer sulle slide)
 * ● Se key.isAcceptable() è verificata, la socketChannel restituita
 * dall'operazione di accept viene registrata sul selettore (con
 * interesse all'operazione di WRITE) e il messaggio viene inviato
 * quando il canale è pronto per la scrittura (key.isWritable è true).
 *
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
class HelloMain {
    final static int DEFAULT_PORT = 9999;

    public static void main(String[] args) throws Exception {
        int myPort = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                myPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Fornire il numero di porta come intero");
                System.exit(-1);
            }
        }
        HelloServer server = new HelloServer(myPort);
        // HelloServer server = new HelloServerImproved(myPort);
        server.start();
    }
}
