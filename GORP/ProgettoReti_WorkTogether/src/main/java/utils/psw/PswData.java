package utils.psw;

/**
 * @author      LUDOVICO VENTURI (UniPi)
 * @date        2021/01/14
 * @versione    1.0
 */

import java.io.Serializable;

public class PswData implements Serializable {
    private static final long serialVersionUID = 01L;

    public byte[] salt;
    public byte[] psw;

    public PswData(byte[] s, byte[] p) {
        this.salt = s;
        this.psw = p;
    }
}