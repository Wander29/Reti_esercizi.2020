package utils;

import protocol.CSProtocol;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class StringUtils {
    /*
       tokenize request string
    */

    public static String byteBufferToString(ByteBuffer buf) {
        String utf8EncodedString = CSProtocol.CHARSET().decode(buf).toString();

        return utf8EncodedString;
    }

    public static byte[] stringToBytes(String s) {
        return s.getBytes(CSProtocol.CHARSET());
    }

    /*
    public static String byteToString(ByteBuffer buf) {
        StringBuilder sbuilder = new StringBuilder();

        while(buf.hasRemaining()) {
            char charRead = (char) buf.get();
            sbuilder.append(charRead);
        }
        return sbuilder.toString();
    }
     */

    public static List<String> tokenizeRequest(ByteBuffer buf) {
        String received = byteBufferToString(buf);

        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(received, ";");

        while(tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        return tokens;
    }

    public static List<String> tokenizeRequest(String s) {
        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(s, ";");

        while(tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        return tokens;
    }

    private static String getFirstToken(String s) {
        StringTokenizer tokenizer = new StringTokenizer(s, ";");

        if(tokenizer.hasMoreTokens())
            return tokenizer.nextToken();

        return null;
    }
}
