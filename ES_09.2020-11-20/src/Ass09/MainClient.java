package Ass09;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/21
 * @version     1.0
 */

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        // aggiunta opzioni accettate
        Options opts = new Options();

        // Option(String opt, String longOpt, boolean hasArg, String description)
        // -p --port <server port>
        Option optPorta = new Option("p", "port", true, "server port");
        optPorta.setRequired(true);
        opts.addOption(optPorta);
        // -n --name <server name>
        Option optNome  = new Option("n", "name", true, "server name");
        optNome.setRequired(true);
        opts.addOption(optNome);

        // parsing degli args
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(opts, args);

            InetAddress serverAddress   = InetAddress.getByName(cmd.getOptionValue("name"));
            int serverPort              = Integer.parseInt(cmd.getOptionValue("port"));

            ClientPing client = new ClientPing(serverAddress, serverPort);
            client.start();

            client.join(CSProtocol.TIMEOUT_JOIN());
        }
        catch (InterruptedException i ) { System.err.println("[MainClient] join interrotta"); }
        catch (MissingArgumentException m) {
            //  ERR -arg x, dove x è il numero dell'argomento
            Option o = m.getOption();
            System.err.printf("ERR --%s -%s <%s>",  o.getLongOpt(),
                                                    o.getOpt(),
                                                    o.getArgName() );
        }
        catch (NumberFormatException n) { System.err.println(ClientErrors.ERR_PORT() + ": invalid argument"); }
        catch (UnknownHostException u)  { System.err.println(ClientErrors.ERR_SERV_NAME() + ": invalid argument"); }
        catch (ParseException e) {

            if(args.length == 0) {
                System.err.println(ClientErrors.ERR_PORT() + ": no arguments");
                System.err.println(ClientErrors.ERR_SERV_NAME() + ": no arguments");
            } else {
                List<String> list = Arrays.asList(args);
                if(! list.contains("-p") || ! list.contains("--port"))
                    System.err.println(ClientErrors.ERR_PORT() + ": no argument");

                if(! list.contains("-n") || ! list.contains("--name"))
                    System.err.println(ClientErrors.ERR_SERV_NAME() + ": no argument");
            }

            formatter.printHelp("java MainClient", opts);
        }
    }
}
