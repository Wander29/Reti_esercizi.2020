import java.io.IOException;
import java.net.*;
import java.util.Calendar;
import java.util.Random;

/**
 * La classe WelcomeServer modella il server del sevizio WelcomeMulticast
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class WelcomeServer {
    /**
     * indirizzo del gruppo di multicast
     */
    private InetAddress multicastGroup;
    /**
     * porta associata all'indirizzo multicast
     */
    private final int port;
    /**
     * messaggio di welcome
     */
    private final String MSG = "WELCOME";

    /**
     *
     * @param addr indirizzo del gruppo di multicast
     * @param port porta a cui associare il socket di multicast
     * @throws UnknownHostException se l'indirizzo non è valido
     * @throws IllegalArgumentException se l'indirizzo non è un indirizzo di multicast
     */
    public WelcomeServer(String addr, int port) throws UnknownHostException, IllegalArgumentException {
        this.multicastGroup = InetAddress.getByName(addr);
        if (!this.multicastGroup.isMulticastAddress())
            throw new IllegalArgumentException();
        this.port = port;
    }

    /**
     * avvia il server
     */
    public void start() {
        Random rand = new Random();
        try (DatagramSocket sock = new DatagramSocket()) {
            while (true) {
                DatagramPacket dat = new DatagramPacket(
                        MSG.getBytes(),
                        MSG.length(),
                        this.multicastGroup,
                        this.port
                );
                sock.send(dat);
                System.out.printf(
                        "Il server ha inviato %s\n",
                        new String(dat.getData(), dat.getOffset(), dat.getLength())
                );
                // attende un numero di millisecondi casuale compreso tra 200 e 2000
                Thread.sleep(rand.nextInt(1800) + 200);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
