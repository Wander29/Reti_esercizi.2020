package Ass08;

import java.net.UnknownHostException;

public class MainClient {
    public static void main(String[] args) {
        int port = 9999;
        String servName = "localhost";

        try {
            ClientEcho client = new ClientEcho(port, servName);
            client.start();
        } catch (UnknownHostException e) {
            System.out.println("Nessun server di nome: " + servName);
            System.exit(1);
        }
    }
}
