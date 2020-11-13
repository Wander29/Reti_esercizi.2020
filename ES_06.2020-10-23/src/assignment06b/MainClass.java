package assignment06b;
/*
Creare un file contenente oggetti che rappresentano i conti correnti di una banca.
    Ogni conto corrente contiene il nome del correntista ed una lista di movimenti.
    I movimenti registrati per un conto corrente sono relativi agli ultimi 2 anni,
    quindi possono essere molto numerosi.
        per ogni movimento vengono registrati la data e la causale del movimento.
        L'insieme delle causali possibili è fissato: Bonifico, Accredito, Bollettino, F24, PagoBancomat.

    NB: Scrivete un programma che crei il file
    Utilizzare NIO per l'interazione con il file e JSON per la serializzazione
 */

import assignment06.Banca;
import assignment06.ContoCorrente;
import assignment06.Movimento;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MainClass {



    public static void main(String args[]) {
        Banca bank = new Banca();
        bank.fillWithStuff();

        // Serializzazione
        ObjectMapper omap = new ObjectMapper();
        try(
                WritableByteChannel wrCh = FileChannel.open(Paths.get("serializ3.json"),
                        StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ) {
            ByteBuffer buf = ByteBuffer.allocateDirect((int) Math.pow(2, 16));

            buf.put(omap.writeValueAsBytes(bank));
            buf.flip();
            while(buf.hasRemaining()) {
                wrCh.write(buf);
            }
        } catch (IOException e) {

        }
    }


}