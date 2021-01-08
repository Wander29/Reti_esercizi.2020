package utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class StringUtils {
    /*
       tokenize request string
    */
    public static List<String> tokenizeRequest(ByteBuffer buf) {
        StringBuilder sbuilder = new StringBuilder();

        while(buf.hasRemaining()) {
            char charRead = (char) buf.get();
            sbuilder.append(charRead);
        }
        String received = sbuilder.toString();

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
}
