package es01;

/*
Scrivere un programma Java che, a partire dal percorso di una directory (es. "/path/to/dir/"),
    recupera il contenuto della directory e delle eventuali sottodirectory.

Il programma scrive in un file di nome “directories” il nome delle directory che incontra
    e nel file “files” il nome dei file.
 */

import java.io.*;

public class MainClass {
    final static String dirname = "/home/ludo/UNIVERSITY/UNI_current/RCL/";
    final static String out_files_name = "files.txt";
    final static String out_dirs_name = "directories.txt";

    public static void main(String[] args) {
        try (
            DataOutputStream directories =
                  new DataOutputStream(
                          new BufferedOutputStream(
                                  new FileOutputStream(out_dirs_name, false)));
            DataOutputStream files =
                    new DataOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(out_files_name, false)));
        ){
            File dir_input = new File(dirname);
            discoverDirectory(dir_input, files, directories);

        } catch (IOException e) {
            System.out.println("ERRORE IO");
        }
    }

    public static void discoverDirectory(
            File dir, DataOutputStream files, DataOutputStream directories) throws IOException
    {
        if(!dir.isDirectory()) {
            System.out.println("Non è una Directory, illuso");
            return;
        }
        File[] content = dir.listFiles();

        for(File f : content) {
            if(f.isDirectory()) {
                directories.writeChars(f.getName() + "\n");
                discoverDirectory(f, files, directories);
            }
            else if(f.isFile()) {
                System.out.println(f.getName());
                files.writeChars(f.getName() + "\n");
            }
        }
    }
}



