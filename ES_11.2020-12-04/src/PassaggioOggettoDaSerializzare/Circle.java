package PassaggioOggettoDaSerializzare;

import java.rmi.*;
public interface Circle extends Remote {
    String SERVICE_NAME = "CircleService";
    Point getCenter() throws RemoteException;
    double getRadius() throws RemoteException;
    void setCircle(Point c, double r) throws RemoteException;
}

