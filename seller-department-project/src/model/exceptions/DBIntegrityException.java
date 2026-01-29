package model.exceptions;

public class DBIntegrityException extends DBException {
    public DBIntegrityException(String msg) {
        super(msg);
    }
    
    public DBIntegrityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}