package PassaggioOggettoDaSerializzare;

import java.rmi.server.UnicastRemoteObject;

public class PointLoc extends UnicastRemoteObject
        implements java.io.Serializable, Point {

    private int x, y;

    public PointLoc (int x, int y) { this.x = x; this.y = y; }

    public void move(int x, int y) {
        this.x += x;
        this.y += y;
        System.out.println("Point has been moved to: " + getCoord());
    }

    public String getCoord() {
        return "[" + x + "," + y + "]";
    }
}

