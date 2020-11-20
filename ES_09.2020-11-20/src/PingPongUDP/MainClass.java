package PingPongUDP;

/**
 * netcat client che manda un messaggio UDP Ping
 * echo -n "PING" | nc -uw 10  -p 50000 localhost 6789
 * nc -ul 50000
 */

public class MainClass {
    public static void main(String args[]) {
        ServerPong server = new ServerPong();
        ClientPing client = new ClientPing();

        server.start();
        client.start();

        System.out.println("Correct Exiting: MAIN");

        try {
            client.join();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
