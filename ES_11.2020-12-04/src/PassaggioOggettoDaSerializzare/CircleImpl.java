package PassaggioOggettoDaSerializzare;

import java.rmi.*;
import java.rmi.server.*;
public class CircleImpl extends UnicastRemoteObject implements Circle
{
    private Point center;
    private double radius;
    public CircleImpl(int x, int y, double r) throws RemoteException {
        setCircle(new PointLoc(x, y), r);
    }

    public void setCircle(Point c, double r) throws RemoteException
    {center = c; radius = r;
        System.out.println("Circle defined - Center: " +
                center.getCoord() + " Radius: " + radius);}
    public Point getCenter() { return center; }
    public double getRadius() { return radius; }
}
