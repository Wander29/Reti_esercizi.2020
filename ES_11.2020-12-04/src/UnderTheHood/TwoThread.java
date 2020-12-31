package UnderTheHood;

public class TwoThread extends Thread{
    ThreadsInt x;
    public TwoThread(ThreadsInt c) {
        this.x=c;
    }
    public void run() {
        try {
            x.methodTwo();}
        catch(Exception e){ System.out.println(e.getMessage());}
    }
}
