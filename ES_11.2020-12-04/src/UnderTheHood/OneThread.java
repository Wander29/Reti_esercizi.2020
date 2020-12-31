package UnderTheHood;

public class OneThread extends Thread {
    ThreadsInt x;
    public OneThread(ThreadsInt c) {
        this.x=c;
    }
    public void run() {
        try {
            x.methodOne();}
        catch(Exception e){ System.out.println(e.getMessage());}
    }
}
