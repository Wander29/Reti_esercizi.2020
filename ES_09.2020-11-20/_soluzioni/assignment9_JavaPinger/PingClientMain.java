import org.apache.commons.cli.*;

/**
 * ASSIGNMENT 09 - JAVA PINGER
 *
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class PingClientMain {
    /**
     * hostname di default
     */
    final static String DEFAULT_SERVER_NAME = "localhost";
    /**
     * porta di default
     */
    final static int DEFAULT_SERVER_PORT = 6789;
    /**
     * timeout di default
     */
    final static int DEFAULT_TIMEOUT = 2000;

    /**
     * @param n numero dell'argomento errato
     */
    public static void __printArgError(int n) {
        System.err.printf("ERR - arg %d\n", n);
        System.exit(1);
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option opt_servername = new Option("n", "server_name", true, "nome del server");
        opt_servername.setRequired(true);
        options.addOption(opt_servername);

        Option opt_serverport = new Option("p", "server_port", true, "numero di porta del server");
        opt_serverport.setRequired(true);
        options.addOption(opt_serverport);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("java PingClientMain", options);
            System.exit(1);
        }

        String server_name = DEFAULT_SERVER_NAME;
        int server_port = DEFAULT_SERVER_PORT;

        if (cmd.getOptionValue("server_name") != null)
            server_name = cmd.getOptionValue("server_name");
        else {
            __printArgError(1);
        }

        try {
            if (cmd.getOptionValue("server_port") != null)
                server_port = Integer.parseInt(cmd.getOptionValue("server_port"));
            else {
                __printArgError(2);
            }
        }
        catch (NumberFormatException e){
            __printArgError(2);
        }
        // crea e avvia il client
        PingClient client = new PingClient(server_port, server_name, DEFAULT_TIMEOUT);
        client.start();
    }

}
