package es01;

public abstract class Counter {
    /**
     * rappresenta il contatore
     */
    protected int contatore = 0;

    public abstract int get();

    public abstract void increment();
}
