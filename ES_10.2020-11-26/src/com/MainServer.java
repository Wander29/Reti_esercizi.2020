package com;

/*
Definire un Server WelcomeServer, che invia su un gruppo di multicast (welcomegroup*), ad
intervalli regolari, un messaggio di «welcome».

Attende tra un invio ed il successivo un intervallo di tempo
simulato mediante il metodo sleep( ).

Definire un client WelcomeClient che si unisce a welcomegroup
e riceve un messaggio di welcome, quindi termina.

 */

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MainServer {

    private static final String     MC_ADDRESS  = "239.255.1.3";
    private static final int        PORT        = 9999;

    public static void main(String[] args) {
        MCServerWelcome server = new MCServerWelcome(MC_ADDRESS, PORT);
        server.start();
    }
}
