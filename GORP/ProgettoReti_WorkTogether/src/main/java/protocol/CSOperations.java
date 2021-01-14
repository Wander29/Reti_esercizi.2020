package protocol;

/*
Client and Server communicate with each other using these operations
    - ';' is the delimiter used in response, request without serialization
 */

public enum CSOperations {
    LOGIN,
    CREATE_PROJECT,
    LIST_PROJECTS,
    SHOW_PROJECT,
    MOVE_CARD,
    ADD_CARD,
    SHOW_MEMBERS,
    ADD_MEMBER,
    CHAT_MSG,
    CHAT_STOP,
    DELETE_PROJECT,
    LOGOUT,
    EXIT;
}
