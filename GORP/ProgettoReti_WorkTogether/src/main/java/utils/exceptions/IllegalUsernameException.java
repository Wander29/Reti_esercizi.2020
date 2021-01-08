package utils.exceptions;

import protocol.CSReturnValues;

public class IllegalUsernameException extends Exception {
    public CSReturnValues retval = CSReturnValues.USERNAME_NOT_PRESENT;

    public IllegalUsernameException() { super(); }
    public IllegalUsernameException(String s) { super(s); }

}
