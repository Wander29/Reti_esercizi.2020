package assignment05;

/**
 * @author      LUDOVICO VENTURI 578033
 * @version     1.0
 * @date        2020-10-22
 */

import java.util.LinkedList;

public class ManagerQueue<E> {
    private final LinkedList<E> queue;
    private final Object termination_obj;

    public <E> ManagerQueue(Object t_o){
        this.queue = new LinkedList<>();
        this.termination_obj = t_o;
    }

    public synchronized void pushSync(E f) {
        this.queue.add(f);
        this.notifyAll();
    }

    /** Tenta di estrarre un elemento dalla coda, implementa l'eccezione di chiusura del canale
     *
     * @return elemento estratto dalla coda
     * @throws InterruptedException
     * @throws MyTerminationProtocolException  Se interecetta il codice di terminazione -> solleva l'ecezione
     */
    public synchronized E pollSync() throws InterruptedException, MyTerminationProtocolException {
        while(this.queue.isEmpty() == true) {
            this.wait();
        }

        // Dynamic Dispatch su equals() garantisce il corretto confronto
        if(this.queue.peek().equals(termination_obj)) {
            throw new MyTerminationProtocolException();
        }
        return this.queue.poll();
    }

    /** inserisce l'ultimo elemento e sveglia tutti
     *
     * @param last_element  ultimo elemento da inserire in coda, si suppone sia un codice che indica la chiusura
     */
    public synchronized void closeQueue(E last_element) throws InterruptedException {
        this.queue.add(last_element);
        this.notifyAll();
    }
}
