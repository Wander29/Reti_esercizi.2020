package utils.exceptions;

import protocol.CSReturnValues;

public class IllegalProjectException extends Exception {
    public CSReturnValues retval = CSReturnValues.PROJECT_NOT_PRESENT;

    public IllegalProjectException() { super(); }
    public IllegalProjectException(String s) { super(s); }

}
