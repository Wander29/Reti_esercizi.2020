package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainClientWT {
    public static void main(String args[]) {

        try {
            Thread th = new Thread(new ClientWT());
            th.start();
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
    }
}
