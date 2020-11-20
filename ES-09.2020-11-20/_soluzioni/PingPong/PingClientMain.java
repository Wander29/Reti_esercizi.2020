/**
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class PingClientMain {

    public static void main(String[] args) {
        PingClient client = new PingClient(6789, "localhost", 2000);
        client.start();
    }

}
