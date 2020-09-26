package com.es01;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Sportelli {
    final int NUM_SPORTELLI     = 5;
    final int NUM_VIAGGIATORI   = 50;
    final int DIM_MAX_CODA      = 10;

    private ThreadPoolExecutor branches;

    public Sportelli() {
        branches = new ThreadPoolExecutor(NUM_SPORTELLI, NUM_SPORTELLI,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(DIM_MAX_CODA));
    }

    /**
     * @param   task        compito da eseguire, viaggiatore
     * @throws RejectedExecutionException       SE la sala è piena
     */
    public void executeTask(Viaggiatore task) throws RejectedExecutionException {
        try {
            branches.execute(task);
        } catch (RejectedExecutionException e) {
            throw new RejectedExecutionException();
        }
    }

    public void gracefulShutdown(long timeout) {
        this.branches.shutdown();
        try {
            while(!this.branches.isTerminated()){
                this.branches.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            System.out.println("awaitTermination interrotta");
        }
    }
}
