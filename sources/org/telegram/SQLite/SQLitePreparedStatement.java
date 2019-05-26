package org.telegram.SQLite;

import java.nio.ByteBuffer;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.NativeByteBuffer;

public class SQLitePreparedStatement {
    private boolean finalizeAfterQuery;
    private boolean isFinalized = false;
    private long sqliteStatementHandle;

    public native void bindByteBuffer(long j, int i, ByteBuffer byteBuffer, int i2) throws SQLiteException;

    public native void bindDouble(long j, int i, double d) throws SQLiteException;

    public native void bindInt(long j, int i, int i2) throws SQLiteException;

    public native void bindLong(long j, int i, long j2) throws SQLiteException;

    public native void bindNull(long j, int i) throws SQLiteException;

    public native void bindString(long j, int i, String str) throws SQLiteException;

    public native void finalize(long j) throws SQLiteException;

    public native long prepare(long j, String str) throws SQLiteException;

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0070 in {6, 9, 12, 15, 18, 19, 21, 23, 25} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public org.telegram.SQLite.SQLiteCursor query(java.lang.Object[] r9) throws org.telegram.SQLite.SQLiteException {
        /*
        r8 = this;
        if (r9 == 0) goto L_0x006a;
        r8.checkFinalized();
        r0 = r8.sqliteStatementHandle;
        r8.reset(r0);
        r0 = 0;
        r1 = 1;
        r7 = 1;
        r1 = r9.length;
        if (r0 >= r1) goto L_0x0064;
        r1 = r9[r0];
        if (r1 != 0) goto L_0x001a;
        r1 = r8.sqliteStatementHandle;
        r8.bindNull(r1, r7);
        goto L_0x0059;
        r2 = r1 instanceof java.lang.Integer;
        if (r2 == 0) goto L_0x002a;
        r2 = r8.sqliteStatementHandle;
        r1 = (java.lang.Integer) r1;
        r1 = r1.intValue();
        r8.bindInt(r2, r7, r1);
        goto L_0x0059;
        r2 = r1 instanceof java.lang.Double;
        if (r2 == 0) goto L_0x003c;
        r2 = r8.sqliteStatementHandle;
        r1 = (java.lang.Double) r1;
        r5 = r1.doubleValue();
        r1 = r8;
        r4 = r7;
        r1.bindDouble(r2, r4, r5);
        goto L_0x0059;
        r2 = r1 instanceof java.lang.String;
        if (r2 == 0) goto L_0x0048;
        r2 = r8.sqliteStatementHandle;
        r1 = (java.lang.String) r1;
        r8.bindString(r2, r7, r1);
        goto L_0x0059;
        r2 = r1 instanceof java.lang.Long;
        if (r2 == 0) goto L_0x005e;
        r2 = r8.sqliteStatementHandle;
        r1 = (java.lang.Long) r1;
        r5 = r1.longValue();
        r1 = r8;
        r4 = r7;
        r1.bindLong(r2, r4, r5);
        r7 = r7 + 1;
        r0 = r0 + 1;
        goto L_0x000d;
        r9 = new java.lang.IllegalArgumentException;
        r9.<init>();
        throw r9;
        r9 = new org.telegram.SQLite.SQLiteCursor;
        r9.<init>(r8);
        return r9;
        r9 = new java.lang.IllegalArgumentException;
        r9.<init>();
        throw r9;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.SQLite.SQLitePreparedStatement.query(java.lang.Object[]):org.telegram.SQLite.SQLiteCursor");
    }

    public native void reset(long j) throws SQLiteException;

    public native int step(long j) throws SQLiteException;

    public long getStatementHandle() {
        return this.sqliteStatementHandle;
    }

    public SQLitePreparedStatement(SQLiteDatabase sQLiteDatabase, String str, boolean z) throws SQLiteException {
        this.finalizeAfterQuery = z;
        this.sqliteStatementHandle = prepare(sQLiteDatabase.getSQLiteHandle(), str);
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

    /* Access modifiers changed, original: 0000 */
    public void checkFinalized() throws SQLiteException {
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
                    FileLog.e(e.getMessage(), e);
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
