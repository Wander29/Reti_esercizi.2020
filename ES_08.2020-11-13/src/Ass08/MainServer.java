package Ass08;
/**
 * @author              VENTURI LUDOVICO 578033
 * @date                2020/11/22
 * @version             1.0
 */

import org.apache.commons.cli.*;

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
        // controlla gli argomenti da linea di comando: necessaria la porta
        Options opts = new Options();
        int serverPort;

        //  -p --port <server port>
        Option optPort = new Option("p", "port", true, "service port");
        optPort.setRequired(true);
        opts.addOption(optPort);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;
        try {
            // parsa gli argomenti
            cmd = parser.parse(opts, args);
            serverPort = Integer.parseInt(cmd.getOptionValue("p"));

            // creazione server ed esecuzione su 1 thread
            ServerEcho server = new ServerEcho(serverPort);
            server.start();

            // server.join(20 * 1000);
            server.join(CSProtocol.TIMEOUT_JOIN_SERVER());
        }
        catch (ParseException | NumberFormatException e) {
            formatter.printHelp("java MainServer", opts);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
