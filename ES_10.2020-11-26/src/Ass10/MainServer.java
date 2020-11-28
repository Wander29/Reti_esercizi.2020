package Ass10;

/**
 * @author              LUDOVICO VENTURI 578033
 * @date                2020/11/28
 * @version             1.0
 */

/*
Definire un Server TimeServer, che

    invia su un gruppo di multicast  dategroup, ad intervalli regolari, la data e l’ora.
    attende tra un invio ed il successivo un intervallo di tempo simulata
    mediante il metodo  sleep().

    L’indirizzo IP di dategroup viene introdotto  da linea di comando.

    Definire quindi un client TimeClient che si unisce a dategroup e riceve,
        per dieci volte consecutive, data ed ora, le visualizza, quindi termina.
 */

/*
esempio     IP:     239.255.1.29
            porta:  9999
 */

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Optional;

public class MainServer {
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

        // variabili supporto
        int port;
        String add;

        try {
            cmd = parser.parse(opts, args);

            port = Integer.parseInt(cmd.getOptionValue("p"));
            add  = cmd.getOptionValue("a");

            // attivazione server
            UDPTimeServer server = new UDPTimeServer(port, add);
            server.start();
        }
        catch (ParseException pe) {
            System.err.println("argomenti non validi --#Exiting#");
            formatter.printHelp("java <.class>", opts); }
        catch (NumberFormatException n)     { System.err.println("valore porta non valido --#Exiting#"); }
        catch (IllegalArgumentException e)  { System.err.println("l'indirizzo passato NON è di multicast --#Exiting#"); }
        catch (UnknownHostException ue)     { System.err.println("indirizzo sconosciuto --#Exiting#"); }
        catch (IOException e)               { System.err.println("IOException --#Exiting#"); }
    }
}
