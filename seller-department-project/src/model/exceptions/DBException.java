package model.exceptions;

import java.sql.SQLException;

public class DBException extends SQLException {
    public DBException(String msg) {
        super(msg);
    }

    public DBException(String msg, Throwable clause) {
        super(msg, clause);
    }
}
