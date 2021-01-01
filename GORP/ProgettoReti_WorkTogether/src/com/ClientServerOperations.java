package com;

import java.util.HashMap;
import java.util.Map;

/*
Client and Server communicate with each other using these operations through a protocol here defined:
    - ';' is the delimiter
    - send just string as byte[], containing all data both for requests and responses
 */

public enum ClientServerOperations {
    LOGIN,
    CREATE_PROJECT,
    LOGOUT;
}
