package com.rcl.msrpg.system.infrastructure.persistence;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

/** Identifica violação de FOREIGN KEY na cadeia de causas (exceções do JDBI envolvem o SQLiteException). */
final class SqliteConstraintViolations {

    private static final String FOREIGN_KEY_MESSAGE = "FOREIGN KEY constraint failed";

    private SqliteConstraintViolations() {}

    static boolean isForeignKeyViolation(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLiteException sqliteError && isForeignKeyCode(sqliteError)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isForeignKeyCode(SQLiteException error) {
        SQLiteErrorCode code = error.getResultCode();
        if (code == SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY) {
            return true;
        }
        String message = error.getMessage();
        return code == SQLiteErrorCode.SQLITE_CONSTRAINT
            && message != null
            && message.contains(FOREIGN_KEY_MESSAGE);
    }

}
