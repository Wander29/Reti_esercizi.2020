import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Random;

/**
 * La classe TimeServer modella il server del sevizio TimeMulticast
 * La classe TimeServer modella il server del sevizio TimeMulticast
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class TimeServer {
    /**
     * indirizzo del gruppo di multicast
     */
    private InetAddress multicastGroup;
    /**
     * porta associata all'indirizzo multicast
     */
    private final int port;
    /**
     * intervallo di tempo simulato tra un invio ed il successivo (ms)
     */
    private final long interval;

    /**
     *
     * @param addr indirizzo del gruppo di multicast
     * @param port porta a cui associare il socket di multicast
     * @param interval intervallo di tempo tra un invio ed il successivo (ms)
     * @throws UnknownHostException se l'indirizzo non è valido
     * @throws IllegalArgumentException se l'indirizzo non è un indirizzo di multicast
     */
    public TimeServer(String addr, int port, long interval) throws UnknownHostException, IllegalArgumentException {
        this.multicastGroup = InetAddress.getByName(addr);
        if (!this.multicastGroup.isMulticastAddress())
            throw new IllegalArgumentException();
        this.port = port;
        this.interval = interval;
    }

    /**
     * avvia il server
     */
    public void start() {
        try (DatagramSocket sock = new DatagramSocket()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            while (true) {
                String timestamp = dtf.format(LocalDateTime.now());
                DatagramPacket dat = new DatagramPacket(
                        timestamp.getBytes(),
                        timestamp.length(),
                        this.multicastGroup,
                        this.port
                );
                sock.send(dat);
                System.out.printf(
                        "%s\n",
                        new String(dat.getData(), dat.getOffset(), dat.getLength())
                );
                Thread.sleep(this.interval);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
