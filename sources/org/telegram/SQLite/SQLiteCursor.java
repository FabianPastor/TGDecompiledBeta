package org.telegram.SQLite;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.NativeByteBuffer;

public class SQLiteCursor {
    public static final int FIELD_TYPE_BYTEARRAY = 4;
    public static final int FIELD_TYPE_FLOAT = 2;
    public static final int FIELD_TYPE_INT = 1;
    public static final int FIELD_TYPE_NULL = 5;
    public static final int FIELD_TYPE_STRING = 3;
    private boolean inRow = false;
    private SQLitePreparedStatement preparedStatement;

    /* access modifiers changed from: package-private */
    public native byte[] columnByteArrayValue(long j, int i);

    /* access modifiers changed from: package-private */
    public native long columnByteBufferValue(long j, int i);

    /* access modifiers changed from: package-private */
    public native int columnCount(long j);

    /* access modifiers changed from: package-private */
    public native double columnDoubleValue(long j, int i);

    /* access modifiers changed from: package-private */
    public native int columnIntValue(long j, int i);

    /* access modifiers changed from: package-private */
    public native int columnIsNull(long j, int i);

    /* access modifiers changed from: package-private */
    public native long columnLongValue(long j, int i);

    /* access modifiers changed from: package-private */
    public native String columnStringValue(long j, int i);

    /* access modifiers changed from: package-private */
    public native int columnType(long j, int i);

    public SQLiteCursor(SQLitePreparedStatement stmt) {
        this.preparedStatement = stmt;
    }

    public boolean isNull(int columnIndex) throws SQLiteException {
        checkRow();
        return columnIsNull(this.preparedStatement.getStatementHandle(), columnIndex) == 1;
    }

    public SQLitePreparedStatement getPreparedStatement() {
        return this.preparedStatement;
    }

    public int intValue(int columnIndex) throws SQLiteException {
        checkRow();
        return columnIntValue(this.preparedStatement.getStatementHandle(), columnIndex);
    }

    public double doubleValue(int columnIndex) throws SQLiteException {
        checkRow();
        return columnDoubleValue(this.preparedStatement.getStatementHandle(), columnIndex);
    }

    public long longValue(int columnIndex) throws SQLiteException {
        checkRow();
        return columnLongValue(this.preparedStatement.getStatementHandle(), columnIndex);
    }

    public String stringValue(int columnIndex) throws SQLiteException {
        checkRow();
        return columnStringValue(this.preparedStatement.getStatementHandle(), columnIndex);
    }

    public byte[] byteArrayValue(int columnIndex) throws SQLiteException {
        checkRow();
        return columnByteArrayValue(this.preparedStatement.getStatementHandle(), columnIndex);
    }

    public NativeByteBuffer byteBufferValue(int columnIndex) throws SQLiteException {
        checkRow();
        long ptr = columnByteBufferValue(this.preparedStatement.getStatementHandle(), columnIndex);
        if (ptr != 0) {
            return NativeByteBuffer.wrap(ptr);
        }
        return null;
    }

    public int getTypeOf(int columnIndex) throws SQLiteException {
        checkRow();
        return columnType(this.preparedStatement.getStatementHandle(), columnIndex);
    }

    public boolean next() throws SQLiteException {
        SQLitePreparedStatement sQLitePreparedStatement = this.preparedStatement;
        int res = sQLitePreparedStatement.step(sQLitePreparedStatement.getStatementHandle());
        if (res == -1) {
            int repeatCount = 6;
            while (true) {
                int repeatCount2 = repeatCount - 1;
                if (repeatCount == 0) {
                    break;
                }
                try {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("sqlite busy, waiting...");
                    }
                    Thread.sleep(500);
                    res = this.preparedStatement.step();
                    if (res == 0) {
                        break;
                    }
                    repeatCount = repeatCount2;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            if (res == -1) {
                throw new SQLiteException("sqlite busy");
            }
        }
        boolean z = res == 0;
        this.inRow = z;
        return z;
    }

    public long getStatementHandle() {
        return this.preparedStatement.getStatementHandle();
    }

    public int getColumnCount() {
        return columnCount(this.preparedStatement.getStatementHandle());
    }

    public void dispose() {
        this.preparedStatement.dispose();
    }

    /* access modifiers changed from: package-private */
    public void checkRow() throws SQLiteException {
        if (!this.inRow) {
            throw new SQLiteException("You must call next before");
        }
    }
}
