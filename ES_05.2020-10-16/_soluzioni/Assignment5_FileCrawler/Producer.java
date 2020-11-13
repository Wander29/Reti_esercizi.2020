import java.io.*;

/**
 * Producer modella il produttore. Il suo ruolo è quello di visitare directory e sottodirectory
 * ed inserire nella coda il nome di ogni directory individuata
 *
 * @author Samuel Fabrizi
 * @version 1.0
 */
public class Producer implements Runnable {
	/**
	 * path della directory di partenza
	 */
	String rootDir;
	/**
	 * coda sincronizzata contenente le directory individuate dal produttore
	 */
	WorkQueue directories;

	/**
	 *
	 * @param pathDir path che individua la directory di partenza
	 * @param q coda delle directory
	 */
	public Producer(String pathDir, WorkQueue q) {
		this.rootDir = pathDir;
		this.directories = q;
	}

	/**
	 *
	 * @param currDir path della directory da processare
	 */
	public void processDirectory(String currDir) {

		File file = new File(currDir);
		if (file.isDirectory()) {
			String[] entries = file.list();
			if (entries != null)
				directories.add(currDir);
			System.out.printf("%-20s: %s\n", "Produttore", currDir);
			for (String entry : entries) {
				if (entry.compareTo(".") == 0 || entry.compareTo("..") == 0)
					continue;
				String subdir = (currDir.endsWith(File.separator)) ? currDir + entry : currDir + File.separator + entry;
				processDirectory(subdir);
			}
		}
	}

	@Override
	public void run() {
		// processa la directory di partenza
		processDirectory(this.rootDir);
		directories.finish();
	}
}
