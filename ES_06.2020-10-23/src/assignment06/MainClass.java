package assignment06;

/*
    Scrivere un programma che rilegge il file e trova, per ogni possibile causale, quanti movimenti hanno
    quella causale.
    progettare un'applicazione che attiva un insieme di thread.
    Uno di essi legge dal file gli oggetti “conto corrente” e li passa, uno per volta,
    ai thread presenti in un thread pool.
    ogni thread calcola il numero di occorrenze di ogni possibile causale all'interno di quel conto corrente
    ed aggiorna un contatore globale.
    alla fine il programma stampa per ogni possibile causale il numero totale di occorrenze.

    Utilizzare NIO per l'interazione con il file e JSON per la serializzazione
 */

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MainClass {
    public static void main(String args[]) {
        try(
                ReadableByteChannel rdCh = FileChannel.open(Paths.get("serializ3.json"), StandardOpenOption.READ)
        ) {
            ByteBuffer buf = ByteBuffer.allocate((int) Math.pow(2, 16));
            ObjectMapper omap = new ObjectMapper();

            StringBuilder tmp = new StringBuilder();

            while(rdCh.read(buf) != -1) {
                buf.flip();
                tmp.append(StandardCharsets.UTF_8.decode(buf)); // necessario per decodificare il buffer
                buf.clear();
            }
            Banca bank = omap.readValue(tmp.toString(), Banca.class);

            TreeMap<String, AtomicInteger> frequenze = new TreeMap<>();
            frequenze.put("bonifico", new AtomicInteger(0));
            frequenze.put("f24", new AtomicInteger(0));
            frequenze.put("pagobancomat", new AtomicInteger(0));
            frequenze.put("bollettino", new AtomicInteger(0));
            frequenze.put("accredito", new AtomicInteger(0));

            ExecutorService threadPool = Executors.newCachedThreadPool();
            ArrayList<ContoCorrente> cc = bank.getConti();
            if(cc != null) {
                for (ContoCorrente conto : cc) {
                    threadPool.submit(new Worker(frequenze, conto));
                }
            }

            // using for-each loop for iteration over Map.entrySet()
            for (Map.Entry<String,AtomicInteger> entry : frequenze.entrySet() )
                System.out.println("Causale = " + entry.getKey() +
                        ", Frequenza = " + entry.getValue());

            while(!threadPool.isTerminated()) {
                threadPool.shutdown();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
