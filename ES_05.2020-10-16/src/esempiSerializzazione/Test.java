package esempiSerializzazione;
import java.io.*;

class A {
    int i = 1;
    public A() {System.out.print("2");}
}

public class Test extends A implements Serializable  {

    public static void main(String[] args) throws Exception {
        Test t1 = new Test ();

        ObjectOutputStream out = new ObjectOutputStream (

                new FileOutputStream("test.txt"));

        out.writeObject(t1);

        ObjectInputStream in = new ObjectInputStream (

                new FileInputStream("test.txt"));

        Test t2 = (Test)in.readObject();
        System.out.println(t2.i); in.close();
    }
}