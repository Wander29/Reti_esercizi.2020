import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.StringTokenizer;

/**
 * RequestManager modella il gestore di una singola richiesta del client.
 * L'unico tipo di richiesta accettata è una richiesta di GET
 *
 * @author Samuel Fabrizi
 * @version 1.2
 */
public class RequestManager implements Runnable {
	private static final String VERSION = "HTTP/1.0 ";
	private static final String STATUS_SUCCESS = "200 OK";
	private static final String STATUS_NOT_FOUND = "404 Not Found";
	private static final String MESSAGE_ERROR = "File not found";
	private static final String CONTENT_TYPE = "Content-Type: ";
	private static final String CONTENT_LENGTH = "Content-Length: ";
	private static final String CR = "\r\n";
	private final Socket client;

	/**
	 *
	 * @param client connection socket utilizzata per la comunicazione con il client
	 */
	public RequestManager(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try (
			// associa gli stream di input (lettura) e output (scrittura) al socket
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
		) {

			// recupera la prima linea della richiesta per ottenere il "method" richiesto
			String requestMessageLine = inFromClient.readLine();
			
			if (requestMessageLine == null) {
				System.out.println("Message null");
				client.close();
				return;
			}

			StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

			// stampa la richiesta del client
			System.out.println("########################");
			System.out.println("Request:\n" + requestMessageLine);

			while (requestMessageLine.length() >= 3) {
				System.out.println(requestMessageLine);
				requestMessageLine = inFromClient.readLine();
			}
			System.out.println("########################");


			if (tokenizedLine.nextToken().equals("GET")) {			// il client ha inviato una richiesta di tipo GET

				String filename = tokenizedLine.nextToken();

				// rimuovi il separatore iniziale se esiste
				if (filename.startsWith("/"))
					filename = filename.substring(1);

				// access the requested file
				File file = new File(filename);

				StringBuilder response = new StringBuilder();
				byte[] messageResponse;

				if (file.exists()){					// il file richiesto esiste (ok)
					int numOfBytes = (int) file.length();
					response.append(VERSION).append(STATUS_SUCCESS).append(CR);

					String mimeType = URLConnection.guessContentTypeFromName(file.getName());

					if (mimeType == null){
						response.append(CONTENT_TYPE).append("text/plain").append(CR);
					}
					else {
						response.append(CONTENT_TYPE).append(mimeType).append(CR);
					}

					response.append(CONTENT_LENGTH).append(numOfBytes).append(CR);

					FileInputStream inFile = new FileInputStream(filename);

					messageResponse = new byte[numOfBytes];
					inFile.read(messageResponse);

					inFile.close();
				}
				else {								// il file richiesto non esiste (not found)
					response.append(VERSION).append(STATUS_NOT_FOUND).append(CR);
					response.append(CONTENT_TYPE).append("text/plain").append(CR);
					response.append(CONTENT_LENGTH).append(MESSAGE_ERROR.length()).append(CR);
					messageResponse = MESSAGE_ERROR.getBytes();
				}
				response.append(CR);

				// invia al client la risposta del server
				outToClient.writeBytes(response.toString());
				outToClient.write(messageResponse, 0, messageResponse.length);

				// stampa la risposta del server
				System.out.println("------------------------");
				System.out.printf("Response:\n%s%s\n", response.toString(), new String(messageResponse));
				System.out.println("------------------------");

			} else {												// il client ha inviato una richiesta != GET
				System.out.println("Bad Request Message");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			// chiusura del socket del client
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
