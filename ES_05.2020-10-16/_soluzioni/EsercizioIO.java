import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Scrivere un programma Java che, a partire dal percorso di una directory (es. "/path/to/dir/"),
 * recupera il contenuto della directory e delle eventuali sottodirectory.
 * Il programma scrive in un file di nome “directories” il nome delle directory che incontra e nel file “files” il nome dei file.
 */

public class EsercizioIO {


    public static void main(String[] args) throws IOException {
        String basedir = "path/to/dir";

        File startDirectory = new File(basedir);

        if(!startDirectory.exists()) {
            System.out.println("Il file iniziale non esiste");
            System.exit(-1);
        }

        if(!startDirectory.isDirectory()) {
            System.out.println("Il file iniziale non è una directory");
            System.exit(-1);
        }

        LinkedList<File> directories = new LinkedList<>();
        directories.add(startDirectory);

        try (
            DataOutputStream d = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("directories"))));
            DataOutputStream f = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("files"))));
        )
        {
            while (directories.size() > 0) {
                File[] filesInCurrentDirectory = directories.poll().listFiles();
                /*
                 * questa condizione risulterà sempre falsa perché la lista directories
                 * contiente SOLO file di tipo directory (a scopo di debugging)
                 */
                if (filesInCurrentDirectory == null){
                    System.out.println("Il file contenuto nella lista non è una directory");
                    System.exit(-1);
                }

                for (File curr_f : filesInCurrentDirectory) {
                    String towrite = curr_f.getName() + "\n";
                    if (curr_f.isDirectory()) {
                        // scrivo il nome della directory in "directories"
                        d.write(towrite.getBytes());
                        // aggiungo la directory alla lista
                        directories.add(curr_f);
                    } else {
                        // scrivo il nome del file in "files"
                        f.write(towrite.getBytes());
                    }
                }
            }
        }
    }
}