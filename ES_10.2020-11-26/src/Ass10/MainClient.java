package Ass10;

import org.apache.commons.cli.Options;

public class MainClient {
    public static void main(String args[]) {
        // controllo argomenti linea comand
        Options opts = new Options();

        int port = 9999;
        String add = "239.255.29.29";

        TimeClient client = new TimeClient(port, add);
        client.start();
    }
}
