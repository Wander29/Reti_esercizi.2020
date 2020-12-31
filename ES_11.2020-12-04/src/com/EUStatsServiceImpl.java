package com;

import java.rmi.*;
// Classes and support for RMI
import java.rmi.server.*;
// Classes and support for RMI servers
import java.util.Hashtable;
// Contains Hashtable class
public class EUStatsServiceImpl extends UnicastRemoteObject implements EUStats {

    class EUData {
        private String Language;
        private int population;
        private String Capital;

        EUData(String Lang, int pop, String Cap) {
            Language = Lang;
            population = pop;
            Capital = Cap;
        }

        String getLangs() {
            return Language;
        }

        int getPop() {
            return population;
        }

        String getCapital() {
            return Capital;
        }
    }

    /* Store data in a hashtable */
    Hashtable<String, EUData> EUDbase = new Hashtable<String, EUData>();

    /* Constructor - set up database */
    EUStatsServiceImpl() throws RemoteException {
        EUDbase.put("France", new EUData("French", 57800000, "Paris"));
        EUDbase.put("United Kingdom", new EUData("English", 57998000, "London"));
        EUDbase.put("Greece", new EUData("Greek", 10270000, "Athens"));
    }

    /* implementazione dei metodi dell'interfaccia */
    public String getMainLanguages(String CountryName) throws RemoteException {
        EUData Data = (EUData) EUDbase.get(CountryName);
        return Data.getLangs();
    }

    public int getPopulation(String CountryName) throws RemoteException {
        EUData Data = (EUData) EUDbase.get(CountryName);
        return Data.getPop();
    }

    public String getCapitalName(String CountryName) throws RemoteException {
        EUData Data = (EUData) EUDbase.get(CountryName);
        return Data.getCapital();
    }
}
