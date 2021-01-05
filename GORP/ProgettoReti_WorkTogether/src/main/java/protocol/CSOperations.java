package protocol;

/*
Client and Server communicate with each other using these operations through a protocol here defined:
    - ';' is the delimiter
    - send just string as byte[], containing all data both for requests and responses
 */

public enum CSOperations {
    LOGIN,
    CREATE_PROJECT,
    LOGOUT,
    EXIT;
}
