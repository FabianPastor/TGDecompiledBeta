package org.telegram.SQLite;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class SQLiteDatabase {
    private boolean inTransaction;
    private boolean isOpen = true;
    private final long sqliteHandle;

    public native void beginTransaction(long j);

    public native void closedb(long j) throws SQLiteException;

    public native void commitTransaction(long j);

    public native long opendb(String str, String str2) throws SQLiteException;

    public long getSQLiteHandle() {
        return this.sqliteHandle;
    }

    public SQLiteDatabase(String str) throws SQLiteException {
        this.sqliteHandle = opendb(str, ApplicationLoader.getFilesDirFixed().getPath());
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
        SQLiteCursor queryFinalized = queryFinalized(str, objArr);
        try {
            if (!queryFinalized.next()) {
                return null;
            }
            Integer valueOf = Integer.valueOf(queryFinalized.intValue(0));
            queryFinalized.dispose();
            return valueOf;
        } finally {
            queryFinalized.dispose();
        }
    }

    public void explainQuery(String str, Object... objArr) throws SQLiteException {
        checkOpened();
        StringBuilder stringBuilder = new StringBuilder();
        String str2 = "EXPLAIN QUERY PLAN ";
        stringBuilder.append(str2);
        stringBuilder.append(str);
        SQLiteCursor query = new SQLitePreparedStatement(this, stringBuilder.toString(), true).query(objArr);
        while (query.next()) {
            int columnCount = query.getColumnCount();
            StringBuilder stringBuilder2 = new StringBuilder();
            for (int i = 0; i < columnCount; i++) {
                stringBuilder2.append(query.stringValue(i));
                stringBuilder2.append(", ");
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(str2);
            stringBuilder3.append(stringBuilder2.toString());
            FileLog.d(stringBuilder3.toString());
        }
        query.dispose();
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
            } catch (SQLiteException e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e(e.getMessage(), e);
                }
            }
            this.isOpen = false;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void checkOpened() throws SQLiteException {
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
