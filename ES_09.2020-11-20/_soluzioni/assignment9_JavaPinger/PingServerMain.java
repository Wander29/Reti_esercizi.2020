import org.apache.commons.cli.*;

/**
 * ASSIGNMENT 09 - JAVA PINGER
 *
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class PingServerMain {
    /**
     * hostname di default
     */
    final static String DEFAULT_NAME = "localhost";

    /**
     * @param n numero dell'argomento errato
     */
    public static void __printArgError(int n) {
        System.err.printf("ERR - arg %d\n", n);
        System.exit(1);
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("p", "port", true, "numero di porta");
        input.setRequired(true);
        options.addOption(input);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("java PingServerMain", options);
            System.exit(1);
        }

        int port = 0;

        try {
            if (cmd.getOptionValue("port") != null)
                port = Integer.parseInt(cmd.getOptionValue("port"));
            else {
                __printArgError(1);
            }
        }
        catch (NumberFormatException e){
            __printArgError(1);
        }

        PingServer server = new PingServer(port, DEFAULT_NAME);
        server.start();
    }

}
