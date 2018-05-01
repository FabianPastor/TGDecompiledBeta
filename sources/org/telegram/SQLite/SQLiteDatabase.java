package org.telegram.SQLite;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class SQLiteDatabase {
    private boolean inTransaction = false;
    private boolean isOpen = false;
    private final long sqliteHandle;

    native void beginTransaction(long j);

    native void closedb(long j) throws SQLiteException;

    native void commitTransaction(long j);

    native long opendb(String str, String str2) throws SQLiteException;

    public long getSQLiteHandle() {
        return this.sqliteHandle;
    }

    public SQLiteDatabase(String str) throws SQLiteException {
        this.sqliteHandle = opendb(str, ApplicationLoader.getFilesDirFixed().getPath());
        this.isOpen = true;
    }

    public boolean tableExists(String str) throws SQLiteException {
        checkOpened();
        if (executeInt("SELECT rowid FROM sqlite_master WHERE type='table' AND name=?;", str) != null) {
            return true;
        }
        return false;
    }

    public SQLitePreparedStatement executeFast(String str) throws SQLiteException {
        return new SQLitePreparedStatement(this, str, true);
    }

    public Integer executeInt(String str, Object... objArr) throws SQLiteException {
        checkOpened();
        str = queryFinalized(str, objArr);
        try {
            if (str.next() == null) {
                return null;
            }
            objArr = Integer.valueOf(str.intValue(null));
            str.dispose();
            return objArr;
        } finally {
            str.dispose();
        }
    }

    public SQLiteCursor queryFinalized(String str, Object... objArr) throws SQLiteException {
        checkOpened();
        return new SQLitePreparedStatement(this, str, true).query(objArr);
    }

    public void close() {
        if (this.isOpen) {
            try {
                commitTransaction();
                closedb(this.sqliteHandle);
            } catch (Throwable e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m2e(e.getMessage(), e);
                }
            }
            this.isOpen = false;
        }
    }

    void checkOpened() throws SQLiteException {
        if (!this.isOpen) {
            throw new SQLiteException("Database closed");
        }
    }

    public void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public void beginTransaction() throws SQLiteException {
        if (this.inTransaction) {
            throw new SQLiteException("database already in transaction");
        }
        this.inTransaction = true;
        beginTransaction(this.sqliteHandle);
    }

    public void commitTransaction() {
        if (this.inTransaction) {
            this.inTransaction = false;
            commitTransaction(this.sqliteHandle);
        }
    }
}
