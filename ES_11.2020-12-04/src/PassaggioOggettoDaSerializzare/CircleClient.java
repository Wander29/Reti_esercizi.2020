package PassaggioOggettoDaSerializzare;

import java.rmi.*;
import java.rmi.registry.*;
public class CircleClient {

    public static void main(String[] args) throws Exception {
        Registry reg = LocateRegistry.getRegistry(9999);

        Circle circle = (Circle) reg.lookup(Circle.SERVICE_NAME);
        System.out.println(circle);

        double r = circle.getRadius() * 2;
        Point p = circle.getCenter();
        System.out.println(p);

        p.move(30, 50);
        System.out.println("Circle - Center: " + p.getCoord() + " Radius: " + r);
        circle.setCircle(p, r);
    }
}
