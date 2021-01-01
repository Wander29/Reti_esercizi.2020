package com;

public class MainClientWT {
    public static void main(String args[]) {
            Thread th = new Thread(new ClientWT());
            th.start();
    }
}
