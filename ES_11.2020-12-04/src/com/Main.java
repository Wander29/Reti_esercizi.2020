package com;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*
Definire un server che implementi, mediante un oggetto remoto, il
seguente servizio:
su richiesta del client, il server restituisce le principali informazioni
relative ad un paese dell'Unione Europea di cui il client ha
specificato il nome:

 linguaggio ufficiale
 popolazione
 nome della capitale

 */

public class Main {

    private static final int PORT = 9999;

    public static void main (String args[]) {
        try {
                /* Creazione di un'istanza dell'oggetto EUStatsServiceImpl */
            EUStatsServiceImpl statsService = new EUStatsServiceImpl();
                /* Esportazione dell'Oggetto */
            // EUStats skeleton = (EUStats) UnicastRemoteObject.exportObject(statsService, 0);
/* Creazione di un registry sulla porta PORT */
            LocateRegistry.createRegistry(PORT);
            Registry r = LocateRegistry.getRegistry(PORT);
/* Pubblicazione dello stub nel registry */
            r.rebind("EUSTATS-SERVER", statsService);
            System.out.println("Server ready");}
        /* If any communication failure occurs... */
        catch (RemoteException e) {
            System.out.println("Communication error " + e.toString());
        }
    }
}
