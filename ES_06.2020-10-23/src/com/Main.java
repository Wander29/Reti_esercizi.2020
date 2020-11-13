package com;

/*
Scrivere un programma che legge, utilizzando NIO (ed in particolare le classi FileChannel e
ByteBuffer), il file di testo (in allegato su moodle) e stampa la frequenza dei caratteri
alfabetici nel testo (conta i caratteri in modo indifferente al fatto che siano minuscoli o
maiuscoli) e scrive le occorrenze dei caratteri in un nuovo file di testo (frequency).

Nota: dato il file in ingresso, potete assumere che ciascun byte che leggete dal buffer
rappresenti un carattere.
 */

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Main {

    /**
     * valore decimale del carattere ASCII "a"
     */
    final static int ASCII_OFFSET = 97;
    /**
     * dimensione dell'alfabeto inglese
     */
    final static int ALPHABET_SIZE = 26;

    public static void main(String[] args) {

        int frequency[] = new int[ALPHABET_SIZE];
        Arrays.fill(frequency, 0);


	    try (
                ReadableByteChannel rdCh = FileChannel.open(Paths.get("/home/ludo/UNIVERSITY/UNI_current/___UNI_WORKSPACE/RETI/ES_06.2020-10-23/lipsum.txt"), StandardOpenOption.READ);
                WritableByteChannel wrCh = FileChannel.open(Paths.get("frequency.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        )
        {
            ByteBuffer cbuf = ByteBuffer.allocateDirect(4096);

            while( rdCh.read(cbuf) != -1 ) {
                cbuf.flip(); // passa in modalitá lettura
                while(cbuf.hasRemaining()) {
                    byte b_tmp = cbuf.get();
                    int c = Character.toLowerCase(b_tmp);
                    int pos = c - ASCII_OFFSET;

                    if(pos >= 0 && pos < ALPHABET_SIZE) {
                        System.out.printf("%c <--> %d\n", (char) c, pos);
                        frequency[pos]++;
                    } else {
                        System.out.printf("cazz ho lett! %c <--> %d\n", (char) c, pos);
                    }
                }
                // passa in modalita scrittura comapattando il buffer
                cbuf.compact();
            }

            cbuf.clear();
            for(int i = 0; i < frequency.length; i++) {
                String line = (char) (ASCII_OFFSET + i) + ": " + frequency[i] + "\n";
                cbuf.put(line.getBytes());
            }

            cbuf.flip();
            while(cbuf.hasRemaining()) {
                wrCh.write(cbuf);
            }

            cbuf.clear();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
