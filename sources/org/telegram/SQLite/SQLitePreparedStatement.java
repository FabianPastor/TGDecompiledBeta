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

    public SQLitePreparedStatement(SQLiteDatabase sQLiteDatabase, String str) throws SQLiteException {
        this.sqliteStatementHandle = prepare(sQLiteDatabase.getSQLiteHandle(), str);
        if (BuildVars.LOGS_ENABLED) {
            this.query = str;
            this.startTime = SystemClock.elapsedRealtime();
        }
    }

    public SQLiteCursor query(Object[] objArr) throws SQLiteException {
        if (objArr != null) {
            checkFinalized();
            reset(this.sqliteStatementHandle);
            int i = 1;
            for (Integer num : objArr) {
                if (num == null) {
                    bindNull(this.sqliteStatementHandle, i);
                } else if (num instanceof Integer) {
                    bindInt(this.sqliteStatementHandle, i, num.intValue());
                } else if (num instanceof Double) {
                    bindDouble(this.sqliteStatementHandle, i, ((Double) num).doubleValue());
                } else if (num instanceof String) {
                    bindString(this.sqliteStatementHandle, i, (String) num);
                } else if (num instanceof Long) {
                    bindLong(this.sqliteStatementHandle, i, ((Long) num).longValue());
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
                long elapsedRealtime = SystemClock.elapsedRealtime() - this.startTime;
                if (elapsedRealtime > 500) {
                    FileLog.d("sqlite query " + this.query + " took " + elapsedRealtime + "ms");
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

    public void bindInteger(int i, int i2) throws SQLiteException {
        bindInt(this.sqliteStatementHandle, i, i2);
    }

    public void bindDouble(int i, double d) throws SQLiteException {
        bindDouble(this.sqliteStatementHandle, i, d);
    }

    public void bindByteBuffer(int i, ByteBuffer byteBuffer) throws SQLiteException {
        bindByteBuffer(this.sqliteStatementHandle, i, byteBuffer, byteBuffer.limit());
    }

    public void bindByteBuffer(int i, NativeByteBuffer nativeByteBuffer) throws SQLiteException {
        bindByteBuffer(this.sqliteStatementHandle, i, nativeByteBuffer.buffer, nativeByteBuffer.limit());
    }

    public void bindString(int i, String str) throws SQLiteException {
        bindString(this.sqliteStatementHandle, i, str);
    }

    public void bindLong(int i, long j) throws SQLiteException {
        bindLong(this.sqliteStatementHandle, i, j);
    }

    public void bindNull(int i) throws SQLiteException {
        bindNull(this.sqliteStatementHandle, i);
    }
}
