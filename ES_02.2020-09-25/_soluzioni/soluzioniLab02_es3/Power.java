import java.util.concurrent.Callable;

/**
* Power modella il task che si occupa dell'elevamento a potenza
* @author Samuel Fabrizi
* @version 1.0
*/
public class Power implements Callable<Double> {
    private final double base;
    private final int power;

    /**
     *
     * @param base base
     * @param power esponente
     */
    public Power(double base,int power){
        this.base = base;
        this.power = power;
    }

    @Override
    public Double call() {
        System.out.format("Esecuzione %f^%d in thread %d%n", this.base, this.power, Thread.currentThread().getId());
        return Math.pow(this.base, this.power);
    }

}