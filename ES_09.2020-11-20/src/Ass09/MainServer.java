package Ass09;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/21
 * @version     1.0
 */

import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * INPUT
 * [#]  accetta un argomento da linea di comando: la porta, che è quella su cui è
 *      attivo il server.
 * [#]  Se uno qualunque degli argomenti è scorretto, stampa un
 *      messaggio di errore del tipo ERR -arg x, dove x è il numero dell'argomento.
 *
 *   Lo scopo di questo assignment è quello di implementare un server PING ed
 *      un corrispondente client PING che consenta al client di misurare il suo RTT
 *      verso il server.
 *
 * il Client  utilizza una comunicazione UDP per comunicare con il server ed invia 10
 *      messaggi al server, con il seguente formato:
 *      PING seqno timestamp
 */

public class MainServer {
    public static void main(String args[]) {
        // aggiunta opzioni accettate
        Options opts = new Options();

        // Option(String opt, String longOpt, boolean hasArg, String description)
        // -p --port <server port>
        Option optPorta = new Option("p", "port", true, "service port");
        optPorta.setRequired(true);
        opts.addOption(optPorta);

        // parsing degli args
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(opts, args);
            int serverPort = Integer.parseInt(cmd.getOptionValue("port"));

            ServerPing server = new ServerPing(serverPort);
            server.start();

            server.join(CSProtocol.TIMEOUT_JOIN());
        }
        catch (InterruptedException i ) { System.err.println("[MainServer] join interrotta"); }
        catch (ParseException | NumberFormatException p) {
            System.err.println("ERR --port -p <service port>\n");
            formatter.printHelp("java MainServer", opts);
        }
    }
}
