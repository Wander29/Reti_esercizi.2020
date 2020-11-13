package esempio2Serializzazione;

import java.io.*;

class A implements Serializable {

    transient int i = 1;

    public A() {System.out.println("2");}

}

public class New extends A implements Serializable {
    int j = 3;

    public New () {System.out.println("4");}

    public static void main(String[] args) throws Exception {

        New t1 = new New ();

        ObjectOutputStream out = new ObjectOutputStream (

                new FileOutputStream("test.txt"));

        out.writeObject(t1);

        ObjectInputStream in = new ObjectInputStream (

                new FileInputStream("test.txt"));

        New t2 = (New)in.readObject();
        System.out.println(t2.i + "" + t2.j); in.close();
    }
}