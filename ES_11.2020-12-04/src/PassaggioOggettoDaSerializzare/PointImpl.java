package PassaggioOggettoDaSerializzare;

import java.rmi.*;
import java.rmi.server.*;
public class PointImpl extends UnicastRemoteObject
        implements Point {
    private int x, y;

    public PointImpl(int x, int y) throws RemoteException {
        this.x = x;
        this.y = y;
    }

    public void move(int x, int y) {
        this.x += x;
        this.y += y;
        System.out.println("Point has been moved to: " +
                getCoord());
    }

    public String getCoord() {
        return "[" + x + "," + y + "]";
    }
}
