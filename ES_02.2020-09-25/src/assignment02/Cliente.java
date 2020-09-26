package assignment02;
/**
 *  @author     LUDOVICO VENTURI 578033
 *  @date       2020/09/25
 */

public class Cliente implements Runnable {

    private final int id_cliente;
    private final int MAX_TEMPO_SERVIZIO = 1500;

    public Cliente(int id) {
        this.id_cliente = id;
    }
    @Override
    public void run() {
        System.out.println("[CLIENTE " + this.id_cliente + "] sta per essere servito");

        try {
            Thread.sleep((int) Math.random() * MAX_TEMPO_SERVIZIO);
        } catch (InterruptedException e) {
            System.out.println("![CLIENTE " + this.id_cliente + "] Sleep interrotta!");
        }

        System.out.println("[CLIENTE " + this.id_cliente + "] è stato servito");
    }

    public int getIdCliente() { return this.id_cliente; }
}
