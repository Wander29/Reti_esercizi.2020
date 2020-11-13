import java.io.*;

/**
 * Worker modella il consumatore. Il suo ruolo è quello di prelevare dalla coda i nomi delle directory
 * e stampare il loro contenuto
 *
 * @author Samuel Fabrizi
 * @version 1.1
 */
public class Worker implements Runnable {

	/**
	 * coda sincronizzata contenente le directory individuate dal produttore
	 */
	private final WorkQueue directories;

	/**
	 *
	 * @param q coda sincronizzata contenente le directory individuate dal produttore
	 */
	public Worker(WorkQueue q) {
		directories = q;
	}

	@Override
	public void run() {
		String name;
		while ((name = directories.remove()) != null) {
			/*
			 * Per come è implementatio il produttore sappiamo già che name
			 * contiene il path di una directory
			 */
			File file = new File(name);
			String[] entries = file.list();
			if (entries == null) // questa condizione può risultare vera se si verifica un errore di I/O
				continue;
			for (String entry : entries) {
				if (entry.compareTo(".") == 0 || entry.compareTo("..") == 0)
					continue;
				String fn = name + File.separator + entry;

				// System.out.printf("%-20s: %s\n", Thread.currentThread().getName(), fn);
				if (!(new File(fn)).isDirectory())
					System.out.printf("%-20s: %s\n", Thread.currentThread().getName(), entry);
			}
		}
	}
}

