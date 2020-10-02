package com.company;

class MyThread extends Thread {
    MyThread(Runnable r) { super(r); }

    // public void run() { System.out.println("Inside Thread"); }
}

class ThreadRunnable implements Runnable {
    public void run()  { System.out.println("Inside Runnable"); }
}

public class domanda11_quiz {

    public static void main(String[] args) {
        new MyThread(new ThreadRunnable()).start();
    }
}
