package protocol.exceptions;

import protocol.CSReturnValues;

public class IllegalOperation extends Exception {
    public CSReturnValues retval = CSReturnValues.ILLEGAL_OPERATION;

    public IllegalOperation() { super(); }
    public IllegalOperation(String s) { super(s); }
}
