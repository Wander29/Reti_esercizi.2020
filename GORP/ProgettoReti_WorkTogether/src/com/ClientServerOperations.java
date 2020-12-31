package com;

import java.util.HashMap;
import java.util.Map;

/*
Client and Server communicate with each other using these operations through a protocol here defined:
    - ';' is the delimiter
    - send just string as byte[], containing all data both for requests and responses
 */

public enum ClientServerOperations {
    LOGIN (0),
    LOGOUT (999);

    public final int OP_CODE;
    private ClientServerOperations(int i) {
        this.OP_CODE = i;
    }

    public int getOP_CODE() {
        return OP_CODE;
    }

    private static Map map = new HashMap<>();

    static {
        for (ClientServerOperations op : ClientServerOperations.values()) {
            map.put(op.OP_CODE, op);
        }
    }

    public static ClientServerOperations valueOf(int code) {
        return (ClientServerOperations) map.get(code);
    }
}
