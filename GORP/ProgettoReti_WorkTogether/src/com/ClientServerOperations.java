package com;

public enum ClientServerOperations {
    LOGIN (0),
    LOGOUT (999);

    public final int OP_CODE;

    private ClientServerOperations(int i) {
        this.OP_CODE = i;
    }
}
