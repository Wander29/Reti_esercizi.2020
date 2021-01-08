package utils.psw;

public class PswData {
    public byte[] salt;
    public byte[] psw;

    public PswData(byte[] s, byte[] p) {
        this.salt = s;
        this.psw = p;
    }
}