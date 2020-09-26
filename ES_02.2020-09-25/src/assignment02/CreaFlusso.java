package assignment02;

import java.util.concurrent.TimeUnit;

public class CreaFlusso extends Thread {
    private Ufficio uf;

    public CreaFlusso(Ufficio ufficio) {
        this.uf = ufficio;
    }

    public void run() {
        int i = 1;
        while(true) {

            if(Thread.interrupted()) {
                System.out.println("CREAFLUSSO: esco..");
                return;
            }

            for(int x = 0; x < 10; x++) {
                this.uf.serviCliente(new Cliente(i++));
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("CREAFLUSSO: interrotto, SLEEP");
                return;
            }
        }
    }
}
