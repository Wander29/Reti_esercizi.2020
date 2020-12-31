package com;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EUStatsClient {
    public static void main (String args[]) {
        EUStats serverObject;
        Remote stub;
        /* Check number of arguments */
        /* If not enough, print usage string and exit */
        if (args.length < 2) {
            System.out.println("usage: java EUStatsClient port countryname");
            return;
        }
        /* Set up a security manager as before */
            /* System.setSecurityManager(new RMISecurityManager()); */
            try {
                System.out.println("sto per cercare nel registro");
                Registry r = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
                stub = r.lookup("EUSTATS-SERVER");
                serverObject = (EUStats) stub;
                System.out.println("oggetto trovato");

                System.out.println("Main language(s) of " + args[1] + "is/are "
                        + serverObject.getMainLanguages(args[1]));
                System.out.println("Population of " + args[1] + " is "
                        + serverObject.getPopulation(args[1]));
                System.out.println("Capital of " + args[1] + " is "
                        + serverObject.getCapitalName(args[1]));
            }
            catch (Exception e) {
                System.out.println("Error in invoking object method " +
                e.toString() + e.getMessage());
                e.printStackTrace();
            }
    }
}
