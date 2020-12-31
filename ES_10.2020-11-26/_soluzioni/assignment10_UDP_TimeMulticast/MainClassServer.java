import org.apache.commons.cli.*;

import java.net.UnknownHostException;

/**
 * Definire un Server TimeServer, che
 *
 * - invia su un gruppo di multicast  dategroup, ad intervalli regolari, la data e l’ora.
 * - attende tra un invio ed il successivo un intervallo di tempo simulata mediante il metodo  sleep().
 * L’indirizzo IP di dategroup viene introdotto  da linea di comando.
 *
 * Definire quindi un client TimeClient che si unisce a dategroup e riceve, per dieci volte consecutive, data ed ora,
 * le visualizza, quindi termina.
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class MainClassServer {
    /**
     * indirizzo del gruppo di multicast
     */
    final static String DEFAULT_TIME_GROUP = "239.255.1.3";
    /**
     * porta associata all'indirizzo di multicast
     */
    final static int DEFAULT_TIME_PORT = 30000;
    /**
     * intervallo di tempo tra un invio ed il successivo
     */
    final static int DEFAULT_INTERVAL = 1000;


    public static void main(String[] args) {

        Options options = new Options();

        Option optMulticastGroup = new Option("m", "multicast_group", true,
                "indirizzo IP del gruppo multicast"
        );
        optMulticastGroup.setRequired(false);
        options.addOption(optMulticastGroup);

        Option optPort = new Option("p", "multicast_port", true,
                "numero di porta associata all'indirizzo di multicast"
        );
        optPort.setRequired(false);
        options.addOption(optPort);

        Option optInterval = new Option("i", "interval", true,
                "intervallo di tempo tra un invio ed il successivo"
        );
        optPort.setRequired(false);
        options.addOption(optInterval);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("java MainClassServer", options);
            System.exit(1);
        }

        try {
            String multicastGroup = DEFAULT_TIME_GROUP;
            int multicastPort = DEFAULT_TIME_PORT;
            int interval = DEFAULT_INTERVAL;

            if (cmd.getOptionValue("multicast_group") != null)
                multicastGroup = cmd.getOptionValue("multicast_group");

            if (cmd.getOptionValue("multicast_port") != null)
                multicastPort = Integer.parseInt(cmd.getOptionValue("multicast_port"));

            if (cmd.getOptionValue("interval") != null)
                interval = Integer.parseInt(cmd.getOptionValue("interval"));

            // crea e avvia il WelcomeServer
            TimeServer server = new TimeServer(multicastGroup, multicastPort, interval);
            server.start();
        }
        catch (UnknownHostException e){             // l'indirizzo passato non è valido
            System.err.println("L'indirizzo immesso non e' valido");
        }
        catch(IllegalArgumentException e){          // l'indirizzo passato non è un indirizzo multicast
            System.err.println("L'indirizzo immesso non e' un indirizzo multicast");
        }
    }


}
