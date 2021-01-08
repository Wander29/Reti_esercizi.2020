package utils;

import java.nio.ByteBuffer;

public class TCPBuffersNIO {

    private final static int BUF_SIZE = 2048;

    public ByteBuffer inputBuf;
    public ByteBuffer outputBuf;
    private String username;

    public TCPBuffersNIO() {
        inputBuf  = ByteBuffer.allocate(BUF_SIZE);
        outputBuf = ByteBuffer.allocate(BUF_SIZE);
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getUsername() {
        return this.username;
    }
}
