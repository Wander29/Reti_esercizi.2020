package com;

import java.nio.ByteBuffer;

public class TCPBuffersNIO {

    private final static int BUF_SIZE = 2048;

    public ByteBuffer inputBuf;
    public ByteBuffer outputBuf;

    public TCPBuffersNIO() {
        inputBuf  = ByteBuffer.allocate(BUF_SIZE);
        outputBuf = ByteBuffer.allocate(BUF_SIZE);
    }
}
