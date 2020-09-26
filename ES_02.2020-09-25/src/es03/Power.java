package es03;

import java.util.concurrent.Callable;

public class Power implements Callable<Double> {

    private double n;
    private int exp;

    public Power(double n, int exp) {
        this.n = n;
        this.exp = exp;
    }

    public Double call() {
        Double res = 0.0;

        System.out.println("Esecuzione {" + this.n + "}^{ " + this.exp + "} in {" +
                Thread.currentThread().getName() + "}");

        res = Math.pow(n, exp);

        return res;
    }
}
