package Ass10;

/**
 * @author              LUDOVICO VENTURI 578033
 * @date                2020/11/28
 * @version             1.0
 */

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainClient {
    public static void main(String args[]) {
        // controllo argomenti linea comand
        Options opts = new Options();

        // -a --address <multicast adrress>
        Option optAddress = new Option("a", "address" ,
                                    true, "multicast address");
        optAddress.setRequired(true);
        opts.addOption(optAddress);

        // -p --port <multicast port>
        Option optPort = new Option("p", "port",
                                    true, "multicast port");
        optPort.setRequired(true);
        opts.addOption(optPort);

        // parsing degli args
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        int port;
        String add;

        try {
            cmd = parser.parse(opts, args);

            port = Integer.parseInt(cmd.getOptionValue("p"));
            add  = cmd.getOptionValue("a");

            // attivazione client
            TimeClient client = new TimeClient(port, add);
            client.start();
        }
        catch (ParseException pe)       {
            System.err.println("argomenti non validi --#Exiting#");
            formatter.printHelp("java <.class>", opts); }
        catch (NumberFormatException n)     { System.err.println("valore porta non valido --#Exiting#"); }
        catch (IllegalArgumentException e)  { System.err.println("l'indirizzo passato NON è di multicast --#Exiting#"); }
        catch (UnknownHostException ue)     { System.err.println("indirizzo sconosciuto --#Exiting#"); }
        catch (IOException e)               { System.err.println("IOException --#Exiting#"); }
        // catch (InterruptedException e)  { e.printStackTrace(); }
    }
}

/*
    ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    for(int i = 0; i < 20 ; i++) {
        tpe.execute(new TimeClient(port, add));
    }

    while(!tpe.isTerminated()) {
        Thread.sleep(1000);
        tpe.shutdown();
    }
*/