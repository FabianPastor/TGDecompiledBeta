package org.telegram.SQLite;

import android.os.SystemClock;
import java.nio.ByteBuffer;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.NativeByteBuffer;

public class SQLitePreparedStatement {
    private boolean isFinalized = false;
    private String query;
    private long sqliteStatementHandle;
    private long startTime;

    /* access modifiers changed from: package-private */
    public native void bindByteBuffer(long j, int i, ByteBuffer byteBuffer, int i2) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native void bindDouble(long j, int i, double d) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native void bindInt(long j, int i, int i2) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native void bindLong(long j, int i, long j2) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native void bindNull(long j, int i) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native void bindString(long j, int i, String str) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native void finalize(long j) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native long prepare(long j, String str) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native void reset(long j) throws SQLiteException;

    /* access modifiers changed from: package-private */
    public native int step(long j) throws SQLiteException;

    public long getStatementHandle() {
        return this.sqliteStatementHandle;
    }

    public SQLitePreparedStatement(SQLiteDatabase db, String sql) throws SQLiteException {
        this.sqliteStatementHandle = prepare(db.getSQLiteHandle(), sql);
        if (BuildVars.LOGS_ENABLED) {
            this.query = sql;
            this.startTime = SystemClock.elapsedRealtime();
        }
    }

    public SQLiteCursor query(Object[] args) throws SQLiteException {
        if (args != null) {
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
                } else if (obj instanceof Long) {
                    bindLong(this.sqliteStatementHandle, i, ((Long) obj).longValue());
                } else {
                    throw new IllegalArgumentException();
                }
                i++;
            }
            return new SQLiteCursor(this);
        }
        throw new IllegalArgumentException();
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
        finalizeQuery();
    }

    /* access modifiers changed from: package-private */
    public void checkFinalized() throws SQLiteException {
        if (this.isFinalized) {
            throw new SQLiteException("Prepared query finalized");
        }
    }

    public void finalizeQuery() {
        if (!this.isFinalized) {
            if (BuildVars.LOGS_ENABLED) {
                long diff = SystemClock.elapsedRealtime() - this.startTime;
                if (diff > 500) {
                    FileLog.d("sqlite query " + this.query + " took " + diff + "ms");
                }
            }
            try {
                this.isFinalized = true;
                finalize(this.sqliteStatementHandle);
            } catch (SQLiteException e) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e(e.getMessage(), (Throwable) e);
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
