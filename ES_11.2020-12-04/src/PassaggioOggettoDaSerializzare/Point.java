package PassaggioOggettoDaSerializzare;

import java.rmi.*;
public interface Point extends Remote {
    public void move(int x, int y) throws RemoteException;
    public String getCoord() throws RemoteException;
}
