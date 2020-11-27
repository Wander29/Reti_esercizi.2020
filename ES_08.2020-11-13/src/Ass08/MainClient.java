package Ass08;

/**
 * @author              VENTURI LUDOVICO 578033
 * @date                2020/11/22
 * @version             1.0
 */

import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainClient {
    public static void main(String[] args) {
        int port;
        String servName = null;

        // controlla gli argomenti da linea di comando: necessaria la porta e il nome del server
        Options opts = new Options();
        int serverPort;

        //  -p --port <server port>
        Option optPort = new Option("p", "port", true, "server port");
        optPort.setRequired(true);
        opts.addOption(optPort);

        //  -n --name <server name>
        Option optName = new Option("n", "name", true, "server name");
        optName.setRequired(true);
        opts.addOption(optName);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;
        try {
            // parsing degli argomenti
            cmd = parser.parse(opts, args);
            serverPort = Integer.parseInt(cmd.getOptionValue("p"));
            servName = cmd.getOptionValue("n");

            InetAddress serverAddress = InetAddress.getByName(servName);

            // creazione ed esecuzione Client
            ClientEcho client = new ClientEcho(serverPort, serverAddress);
            client.start();

            client.join(CSProtocol.TIMEOUT_JOIN_SERVER());
            /*
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
            for(int i=0; i < 20; i++) {
                tpe.execute(new ClientEcho(serverPort, serverAddress));
            }
            while(!tpe.isTerminated()) {
                tpe.shutdown();
                Thread.sleep(2000);
            }
            */
        }
        catch (ParseException | NumberFormatException e) { formatter.printHelp("java MainServer", opts); }
        catch (UnknownHostException e) { System.out.println("Nessun server di nome: " + servName); }
        catch (InterruptedException e) {  }
    }
}
