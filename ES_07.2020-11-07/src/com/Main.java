package com;

/*
Scrivere un programma JAVA che implementi un server che apre una serversocket su una porta
 e sta in attesa di richieste di connessione.

Quando arriva una richiesta di connessione, il server accetta la connessione,
    trasferisce al client un file e poi chiude la connessione.

Ulteriori dettagli:
-          Il server gestisce una richiesta per volta
-          Il server invia sempre lo stesso file, usate un file di testo
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    Socket sockClient = null;
    DataOutputStream writer = null;
    FileInputStream fis = null;

    public static void main(String[] args) {

        File fin = new File("test.txt");

        try(ServerSocket sockListen = new ServerSocket(9999)) {
            //sockListen.bind(new InetSocketAddress(InetAddress.getLocalHost(), 9999));

            // while(true)
            try(
                    Socket sockClient = sockListen.accept();
                    DataOutputStream writer = new DataOutputStream(sockClient.getOutputStream());
                    FileInputStream fis = new FileInputStream(fin);
            ) {
                // prova ad inviare il file poi chiude la connessione
                    int n_bytes = (int) fin.length();
                    byte[] fileInBytes = new byte[n_bytes];
                    fis.read(fileInBytes);

                    writer.write(fileInBytes, 0, n_bytes);
                    writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
