package model.exceptions;

public class EntityNotFoundException extends DBException {
    public EntityNotFoundException(String msg) {
        super(msg);
    }
}