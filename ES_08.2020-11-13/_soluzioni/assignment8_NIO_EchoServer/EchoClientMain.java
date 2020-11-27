/**
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class EchoClientMain {
    final static int DEFAULT_PORT = 9999;

    public static void main(String[] args){
        int myPort = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                myPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Fornire il numero di porta come intero");
                System.exit(-1);
            }
        }
        // crea e avvia il client
        EchoClient client = new EchoClient(myPort);
        client.start();
    }

}
