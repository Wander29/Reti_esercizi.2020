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

import static ass07.HTTPResponse.*;

/******************************************************************************
 * Struttura
 * - é un Thread che gestisce 1 comunicazione con un client, rispondendo
 * - gestisce la GET fornendo la risorsa richiesta
 * - gestisce le altre inviando 404, rimandando all'index
 * - a seconda del valore di "Boolean persistent" si ha una connessione persistente o meno
 *      (sempre HTTP/1.1)
 *****************************************************************************/

public class ClientHandler implements Runnable {

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
            this.indexPageBytes = new String("<html>\n" +
                    "<head>\n" +
                    "<title>File not found</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h1>404. File Not Found</h1>\n" +
                    "</body>\n" +
                    "</html>").getBytes();
        }
    }

    @Override
    public void run() {
        boolean persistent = true; // indica se la connessione è persistente o ascolta 1 richiesta per volta
        String requestLine;

         // associa gli stream di Input e Output della comunicazione via Socket
        try (
            InputStreamReader   isrCliente = new InputStreamReader(this.sockCliente.getInputStream());
            BufferedReader      inCliente = new BufferedReader(isrCliente);
            OutputStream        outCliente = this.sockCliente.getOutputStream();
        ) {
            /************************************
             *      (stream aperti)
             * legge la richiesta HTTP
             * la elabora
             * la invia
             ************************************/
            do {
                requestLine = inCliente.readLine();
                if(requestLine == null || requestLine.isEmpty())   break;
                else                        this.computeRequest(requestLine, outCliente);
            } while(persistent == true);

        } catch (IOException e) {
            System.out.println("Errore durante l'apertura degli stream iniziali");
            return;
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
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
    private void computeRequest(String reqLine, OutputStream outStream) throws IOException {
        // tokenizzazione e archiviazione della Request

        StringTokenizer st = new StringTokenizer(reqLine, " ");
        /* versione che tokenizza tutta la richiesta
        ArrayList<String> reqLineToStrings = new ArrayList<>();
        while(st.hasMoreTokens()) {
            reqLineToStrings.add(st.nextToken());
        }
        */

        // versione che prende solamente i primi 2 token (caso migliore)
        String header = "", fileRequestedName = "";
        if(st.hasMoreTokens()) { header = st.nextToken(); }
        else return;


        HTTPResponse response = new HTTPResponse();
        // a seconda del tipo di richiesta
        switch(header) {
            case "GET":
                StringBuilder strBuilderTMP = new StringBuilder(this.BASEDIR);
                if(st.hasMoreTokens()) { fileRequestedName = st.nextToken(); }
                else return;
                strBuilderTMP.append(fileRequestedName);

                /************************************
                 * opens requested file
                 *      IF file not found -> 404 error
                 * reads it as byte[]
                 * sets code 200 OK write HTTP/1.1 200 OK\r\nServer: Rancoroso\r\nContent-Type: ..\r\n\r\n
                 * writes into the OutputStream
                 ************************************/
                String tmp = strBuilderTMP.toString(); // conterrà il path del file richiesto

                File fileRequested = new File(strBuilderTMP.toString());
                try (FileInputStream fisRequested = new FileInputStream(fileRequested)) {
                    byte fileAsBytes[] = fisRequested.readAllBytes();

                    // inizio della costruzione del messagggio HTTP
                    sendHTTPResponse(response, STATUS_CODE.OK, extractExtensionFromString(fileRequested.getName()), fileAsBytes, outStream);

                } catch (FileNotFoundException e) {
                    // se il file non è presente
                    sendHTTPResponse(response, STATUS_CODE.NOT_FOUND, extractExtensionFromString(indexPagePath), indexPageBytes, outStream);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            default: ;
        }
    }

    private void sendHTTPResponse(HTTPResponse response, STATUS_CODE sc,
                                  String extension, byte[] fileInBytes,
                                  OutputStream outStream)
            throws IOException {
        response.setStatusCode(sc);

        response.setContentType(extractExtensionFromString(extension));
        response.insertEmptyLine(); // termine intestazioni
        String headerHTTPResponse = response.toString();

        // scrittura sullo stream della Response (header + data)
        outStream.write(headerHTTPResponse.getBytes());
        outStream.write(fileInBytes);
        outStream.flush();
    }

    private String extractExtensionFromString(String s) {
        return s.substring(s.lastIndexOf("."));
    }
}
