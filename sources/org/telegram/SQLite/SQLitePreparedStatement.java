package org.telegram.SQLite;

import java.nio.ByteBuffer;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.NativeByteBuffer;

public class SQLitePreparedStatement {
    private boolean finalizeAfterQuery = false;
    private boolean isFinalized = false;
    private long sqliteStatementHandle;

    native void bindByteBuffer(long j, int i, ByteBuffer byteBuffer, int i2) throws SQLiteException;

    native void bindDouble(long j, int i, double d) throws SQLiteException;

    native void bindInt(long j, int i, int i2) throws SQLiteException;

    native void bindLong(long j, int i, long j2) throws SQLiteException;

    native void bindNull(long j, int i) throws SQLiteException;

    native void bindString(long j, int i, String str) throws SQLiteException;

    native void finalize(long j) throws SQLiteException;

    native long prepare(long j, String str) throws SQLiteException;

    native void reset(long j) throws SQLiteException;

    native int step(long j) throws SQLiteException;

    public long getStatementHandle() {
        return this.sqliteStatementHandle;
    }

    public SQLitePreparedStatement(SQLiteDatabase db, String sql, boolean finalize) throws SQLiteException {
        this.finalizeAfterQuery = finalize;
        this.sqliteStatementHandle = prepare(db.getSQLiteHandle(), sql);
    }

    public SQLiteCursor query(Object[] args) throws SQLiteException {
        if (args == null) {
            throw new IllegalArgumentException();
        }
        checkFinalized();
        reset(this.sqliteStatementHandle);
        int i = 1;
        for (Object obj : args) {
            if (obj == null) {
                bindNull(this.sqliteStatementHandle, i);
            } else if (obj instanceof Integer) {
                bindInt(this.sqliteStatementHandle, i, ((Integer) obj).intValue());
            } else if (obj instanceof Double) {
                bindDouble(this.sqliteStatementHandle, i, ((Double) obj).doubleValue());
            } else if (obj instanceof String) {
                bindString(this.sqliteStatementHandle, i, (String) obj);
            } else {
                throw new IllegalArgumentException();
            }
            i++;
        }
        return new SQLiteCursor(this);
    }

    public int step() throws SQLiteException {
        return step(this.sqliteStatementHandle);
    }

    public SQLitePreparedStatement stepThis() throws SQLiteException {
        step(this.sqliteStatementHandle);
        return this;
    }

    public void requery() throws SQLiteException {
        checkFinalized();
        reset(this.sqliteStatementHandle);
    }

    public void dispose() {
        if (this.finalizeAfterQuery) {
            finalizeQuery();
        }
    }

    void checkFinalized() throws SQLiteException {
        if (this.isFinalized) {
            throw new SQLiteException("Prepared query finalized");
        }
    }

    public void finalizeQuery() {
        if (!this.isFinalized) {
            try {
                this.isFinalized = true;
                finalize(this.sqliteStatementHandle);
            } catch (SQLiteException e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m2e(e.getMessage(), e);
                }
            }
        }
    }

    public void bindInteger(int index, int value) throws SQLiteException {
        bindInt(this.sqliteStatementHandle, index, value);
    }

    public void bindDouble(int index, double value) throws SQLiteException {
        bindDouble(this.sqliteStatementHandle, index, value);
    }

    public void bindByteBuffer(int index, ByteBuffer value) throws SQLiteException {
        bindByteBuffer(this.sqliteStatementHandle, index, value, value.limit());
    }

    public void bindByteBuffer(int index, NativeByteBuffer value) throws SQLiteException {
        bindByteBuffer(this.sqliteStatementHandle, index, value.buffer, value.limit());
    }

    public void bindString(int index, String value) throws SQLiteException {
        bindString(this.sqliteStatementHandle, index, value);
    }

    public void bindLong(int index, long value) throws SQLiteException {
        bindLong(this.sqliteStatementHandle, index, value);
    }

    public void bindNull(int index) throws SQLiteException {
        bindNull(this.sqliteStatementHandle, index);
    }
}
