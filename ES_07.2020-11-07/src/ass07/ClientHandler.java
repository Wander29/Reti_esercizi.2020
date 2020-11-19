package ass07;

/**
 * @author      LUDOVICO VENTURI 578033
 * @date        2020/11/19
 * @version1    1.0
 */

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

/******************************************************************************
 * 
 *****************************************************************************/

public class ClientHandler implements Runnable {

    private BufferedReader  inCliente;
    private OutputStream    outCliente;
    private static final String BASEDIR = System.getProperty("user.dir") + "/www";

    final static String indexPagePath = "www/index.html";
    private byte[] indexPageBytes;

    private Socket sockCliente; // socket di comunicazione col client

    public ClientHandler(Socket cl) {
        this.sockCliente = cl;

        FileInputStream fis = null;

        try {
            // prova a pre-caricare l'indice
            fis = new FileInputStream(new File(indexPagePath));
            this.indexPageBytes = fis.readAllBytes();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setInputBuffer(BufferedReader br) {
        this.inCliente = br;
    }

    @Override
    public void run() {
        /************************************
         * associa gli stream di Input e Output della comunicazione via Socket
         ************************************/
        try {
            InputStreamReader isrCliente = new InputStreamReader(this.sockCliente.getInputStream());
            this.inCliente = new BufferedReader(isrCliente);
            this.outCliente = this.sockCliente.getOutputStream();

        } catch (IOException e) {
            System.out.println("");
        }

        /************************************
        * legge la richiesta HTTP
         * la elabora
         * la invia
         ************************************/
        boolean persistent = true;
        String requestLine;
        try {
            do {
                requestLine = inCliente.readLine();
                if(requestLine.isEmpty())   break;
                else                        this.computeRequest(requestLine);
            } while(persistent == true);
            inCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** scompone la richiesta in token, esempio:
     * tok 0: GET
     * tok 1: file.txt
     * tok 2: HTTP
     * tok 3: 1.1
     * tok 4: Host:
     * tok 5: localhost:9999
     *
     * @param reqLine
     */
    private void computeRequest(String reqLine) throws IOException {
        // tokenizzazione e archiviazione della Request
        StringTokenizer st = new StringTokenizer(reqLine, " ");
        ArrayList<String> reqLineToStrings = new ArrayList<>();
        while(st.hasMoreTokens()) {
            reqLineToStrings.add(st.nextToken());
        }

        // a seconda del tipo di richiesta
        switch(reqLineToStrings.get(0)) {
            case "GET":
                StringBuilder strBuilderTMP = new StringBuilder(this.BASEDIR);
                strBuilderTMP.append(reqLineToStrings.get(1));

                /************************************
                 * opens requested file
                 *      IF file not found -> 404 error
                 * reads it as byte[]
                 * sets code 200 OK write HTTP/1.1 200 OK\r\nServer: Rancoroso\r\nContent-Type: ..\r\n\r\n
                 * writes into the OutputStream
                 ************************************/
                HTTPResponse response = new HTTPResponse();
                String tmp = strBuilderTMP.toString(); // conterrà il path del file richiesto

                File fileRequested = new File(strBuilderTMP.toString());
                try (FileInputStream fisRequested = new FileInputStream(fileRequested)) {
                    byte fileAsBytes[] = fisRequested.readAllBytes();

                    // inizio della costruzione del messagggio HTTP
                    response.setStatusCode(200);
                    response.setContentType(extractExtensionFromString(fileRequested.getName()));
                    response.insertEmptyLine(); // termine delle intestazioni

                    String headerHTTPResponse = response.toString();

                    // scrittura sullo stream della Response (header + data)
                    outCliente.write(headerHTTPResponse.getBytes());
                    outCliente.write(fileAsBytes);

                } catch (FileNotFoundException e) {
                    // se il file non è presente
                    System.out.println("Apertura file richiesta: non c'è sto file -> 404");
                    response.setStatusCode(404);

                    response.setContentType(extractExtensionFromString(indexPagePath));
                    response.insertEmptyLine();

                    String headerHTTPResponse = response.toString();
                    System.out.println(headerHTTPResponse);
                    outCliente.write(headerHTTPResponse.getBytes());
                    outCliente.write(this.indexPageBytes);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                outCliente.flush();

                break;
            default: ;
        }

    }

    private String extractExtensionFromString(String s) {
        return s.substring(s.lastIndexOf("."));
    }
}
