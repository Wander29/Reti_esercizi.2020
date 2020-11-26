package com;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MainClient {

    private static final String     MC_ADDRESS  = "239.255.1.3";
    private static final int        PORT        = 9999;

    public static void main(String[] args) {
        MCClientWelcome client = new MCClientWelcome(MC_ADDRESS, PORT);
        client.start();
    }
}
