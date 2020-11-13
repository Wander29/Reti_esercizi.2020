import java.io.*;
import java.net.*;
import java.util.*;

class FileServer
{
	public static void main (String args[]) throws Exception
	{
		String fileName= "file.txt";

		// check if a port number is given as the first command line argument
		// if not argument is given, use port number 6789
		int myPort = 6789;
		if (args.length > 0)
		{
			try {
				myPort = Integer.parseInt(args[0]);
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				System.out.println("Need port number as argument");
				System.exit(-1);
			}
			catch (NumberFormatException e)
			{
				System.out.println("Please give port number as integer.");
				System.exit(-1);
			}
		}

		// set up connection socket
		ServerSocket listenSocket = new ServerSocket (myPort);

		// listen (i.e. wait) for connection request
		System.out.println ("Web server waiting for request on port " + myPort);

		while (true) {

			Socket connectionSocket = listenSocket.accept();

			// set up the read and write end of the communication socket
			BufferedReader inFromClient = new BufferedReader (
					new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream (
					connectionSocket.getOutputStream());

			// retrieve first line of request and set up for parsing
	//		fileName = fileName.substring(1);


			// access the requested file
			File file = new File(fileName);

			// convert file to a byte array
			int numOfBytes = (int) file.length();
			try (FileInputStream inFile = new FileInputStream (fileName)){

				byte[] fileInBytes = new byte[numOfBytes];
				inFile.read(fileInBytes);    
				outToClient.write(fileInBytes, 0, numOfBytes);
				outToClient.flush();
			}
			catch(FileNotFoundException e) {
				String message = "File not Found";
				outToClient.writeBytes(message);
			}
			catch(IOException e1) {
				e1.printStackTrace();
			}

			connectionSocket.close();
		}
	}


}


