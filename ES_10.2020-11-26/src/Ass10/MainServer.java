package Ass10;

/*
Definire un Server TimeServer, che

    invia su un gruppo di multicast  dategroup, ad intervalli regolari, la data e l’ora.
    attende tra un invio ed il successivo un intervallo di tempo simulata
    mediante il metodo  sleep().

    L’indirizzo IP di dategroup viene introdotto  da linea di comando.

    Definire quindi un client TimeClient che si unisce a dategroup e riceve,
        per dieci volte consecutive, data ed ora, le visualizza, quindi termina.
 */

import org.apache.commons.cli.*;

import java.util.Optional;

public class MainServer {
    public static void main(String args[]) {
        // controllo argomenti linea comand
        Options opts = new Options();

        int port = 9999;
        String add = "239.255.29.29";

        UDPTimeServer server = new UDPTimeServer(port, add);
        server.start();
    }
}
