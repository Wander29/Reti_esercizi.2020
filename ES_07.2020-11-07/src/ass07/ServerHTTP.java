package ass07;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/19
 * @version1    1.0
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/********************************************************************
 * Struttura:
 * - ThreadPool di ClientHandler
 * - ServerSocket in ascolto
 *
 *      - Socket client per ogni nuovo client
 *      - submit del task al ThreadPool
 *******************************************************************/

public class ServerHTTP {
    private int PORT;

    public ServerHTTP(int port) {
        this.PORT = port;
    }

    public void start() {
        try( ServerSocket servSock = new ServerSocket(this.PORT) ) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            while(true) {
                Socket cliente = servSock.accept();
                System.out.println("NUOVO SOCKET!");
                tpe.execute(new ClientHandler(cliente));
            }
        } catch (IOException e) {
            System.out.println("Impossibile stabilire una connessione col client");
        }
    }
}
