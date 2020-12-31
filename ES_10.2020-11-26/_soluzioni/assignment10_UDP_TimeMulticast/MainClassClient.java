import org.apache.commons.cli.*;

import java.net.UnknownHostException;

/**
 * Definire un client WelcomeClient che si unisce a welcomegroup, riceve un messaggio di welcome, quindi termina.
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class MainClassClient {
    /**
     * indirizzo di broadcast
     */
    final static String DEFAULT_TIME_GROUP = "239.255.1.3";
    /**
     * porta associata all'indirizzo di multicast
     */
    final static int DEFAULT_TIME_PORT = 30000;


    public static void main(String[] args){
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

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("java MainClassClient", options);
            System.exit(1);
        }

        try {
            String multicastGroup = DEFAULT_TIME_GROUP;
            int multicastPort = DEFAULT_TIME_PORT;

            if (cmd.getOptionValue("multicast_group") != null)
                multicastGroup = cmd.getOptionValue("multicast_group");

            if (cmd.getOptionValue("multicast_port") != null)
                multicastPort = Integer.parseInt(cmd.getOptionValue("multicast_port"));

            // crea e avvia il WelcomeClient
            TimeClient client = new TimeClient(multicastGroup, multicastPort);
            client.start();
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        catch (UnknownHostException e){             // l'indirizzo passato non è valido
            System.err.println("L'indirizzo immesso non e' valido");
        }
        catch(IllegalArgumentException e){          // l'indirizzo passato non è un indirizzo multicast
            System.err.println("L'indirizzo immesso non e' un indirizzo multicast");
        }
    }
}
