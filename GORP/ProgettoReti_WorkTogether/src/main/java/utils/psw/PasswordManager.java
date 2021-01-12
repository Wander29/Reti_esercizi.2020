package utils.psw;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class PasswordManager {
    private static SecureRandom random = new SecureRandom();
    private static byte[] salt = new byte[16];

    /*
    based on:
        @https://www.baeldung.com/java-password-hashing
     */

    private static byte[] hash(String s, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(s.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        return hash;
    }

    public static PswData hashPsw(String s) throws NoSuchAlgorithmException, InvalidKeySpecException {
        random.nextBytes(salt);
        return new PswData(salt, PasswordManager.hash(s, salt));
    }

    public static boolean comparePsw(String s, PswData psw) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Arrays.equals(PasswordManager.hash(s, psw.salt), psw.psw);
    }
}
