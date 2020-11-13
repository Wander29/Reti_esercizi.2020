import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Scrivere un programma che legge, utilizzando NIO (ed in particolare le classi FileChannel e ByteBuffer),
 * il file di testo (in allegato) e stampa la frequenza dei caratteri alfabetici nel testo
 * (conta i caratteri in modo indifferente al fatto che siano minuscoli o maiuscoli).
 *
 * Nota: dato il file in ingresso, potete assumere che ciascun byte che leggete dal buffer rappresenti un carattere.
 * Per comodità trovate qui sotto la tabella del set di caratteri US-ASCII.
 *
 *
 * @version 1.1
 */
public class ContaCaratteri {

	/**
	 * valore decimale del carattere ASCII "a"
	 */
	final static int ASCII_OFFSET = 97;
	/**
	 * dimensione dell'alfabeto inglese
	 */
	final static int ALPHABET_SIZE = 26;

	public static void main(String[] args) throws IOException {

		int[] frequency = new int[ALPHABET_SIZE];

		// inizializzazione l'array delle frequenze
		Arrays.fill(frequency, 0);

		// creazione del buffer
		ByteBuffer buffer=ByteBuffer.allocateDirect(4096);
		
		//read input file
		try (ReadableByteChannel readChannel= FileChannel.open(Paths.get("lipsum"), StandardOpenOption.READ);
				WritableByteChannel outChannel= FileChannel.open(Paths.get("frequency"), StandardOpenOption.WRITE, StandardOpenOption.CREATE)){ 
			
			while(readChannel.read(buffer)!=-1) {
				// prepara il buffer per una write/get
				buffer.flip();
				while(buffer.hasRemaining()) { // esistono ancora elementi tra la posizione corrente e limit
					byte ch = buffer.get();
					// converte il carattere in lower case
					int c = Character.toLowerCase(ch);
					int pos = c - ASCII_OFFSET;
					if(pos >= 0 && pos < 26) {    // il carattere è un carattere alfabetico
						System.out.printf("%c <-> %d\n", (char)c, pos);
						// incremento la frequenza del carattere alfabetico trovato
						frequency[pos]++;
					}
					else{
						System.out.printf("[else] %c <-> %d\n", (char)c, pos);
					}
				}
				buffer.compact();
			}
			
			for(int f: frequency) {
				buffer.clear();
				String line = f + "\n";
				buffer.put(line.getBytes());
				buffer.flip();
				while(buffer.hasRemaining()) {
					// scrive il contenuto di buffer nel file
					outChannel.write(buffer);
				}
			}
			buffer.clear();
		}
	}

}