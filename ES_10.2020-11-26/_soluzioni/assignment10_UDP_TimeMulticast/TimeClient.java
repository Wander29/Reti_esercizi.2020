import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;

/**
 * La classe TimeClient modella il server del servizio TimeMulticast
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class TimeClient {
    /**
     * indirizzo del gruppo di multicast
     */
    private final InetAddress welcomeGroup;
    /**
     * porta associata all'indirizzo multicast
     */
    private final int port;
    /**
     * lunghezza del messaggio da ricevere (possiamo avere questa informazione)
     */
    private final int MSG_LENGTH = 128;
    /**
     * numero di messaggi consecutivi del TimeServer che vogliamo recuperare
     */
    private final int N_DATE = 10;


    /**
     *
     * @param addr indirizzo del gruppo di multicast
     * @param port porta a cui associare il socket di multicast
     * @throws UnknownHostException se l'indirizzo non è valido
     * @throws IllegalArgumentException se l'indirizzo non è un indirizzo di multicast
     */
    public TimeClient(String addr, int port) throws UnknownHostException, IllegalArgumentException{
        this.welcomeGroup = InetAddress.getByName(addr);
        // verifica che l'indirizzo passato come argomento sia valido
        if (!this.welcomeGroup.isMulticastAddress())
            throw new IllegalArgumentException();
        this.port = port;
    }

    /**
     * avvia il client
     */
    public void start(){
        try (MulticastSocket multicastWelcome = new MulticastSocket(this.port)) {
            multicastWelcome.joinGroup(this.welcomeGroup);
            for (int i=0; i<this.N_DATE; i++){
                DatagramPacket dat = new DatagramPacket(new byte[this.MSG_LENGTH], MSG_LENGTH);
                multicastWelcome.receive(dat);
                System.out.printf(
                        "%d: %s\n",
                        i,
                        new String(dat.getData(), dat.getOffset(), dat.getLength())
                );
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
