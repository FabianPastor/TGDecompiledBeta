package org.telegram.messenger;

import android.os.Looper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
/* loaded from: classes.dex */
public class FilePathDatabase {
    private static final String DATABASE_BACKUP_NAME = "file_to_path_backup";
    private static final String DATABASE_NAME = "file_to_path";
    private static final int LAST_DB_VERSION = 2;
    private File cacheFile;
    private final int currentAccount;
    private SQLiteDatabase database;
    private final DispatchQueue dispatchQueue;
    private File shmCacheFile;

    public FilePathDatabase(int i) {
        this.currentAccount = i;
        DispatchQueue dispatchQueue = new DispatchQueue("files_database_queue_" + i);
        this.dispatchQueue = dispatchQueue;
        dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FilePathDatabase.this.lambda$new$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        createDatabase(0, false);
    }

    public void createDatabase(int i, boolean z) {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File file = new File(filesDirFixed, "account" + this.currentAccount + "/");
            file.mkdirs();
            filesDirFixed = file;
        }
        this.cacheFile = new File(filesDirFixed, "file_to_path.db");
        this.shmCacheFile = new File(filesDirFixed, "file_to_path.db-shm");
        boolean z2 = !this.cacheFile.exists();
        try {
            SQLiteDatabase sQLiteDatabase = new SQLiteDatabase(this.cacheFile.getPath());
            this.database = sQLiteDatabase;
            sQLiteDatabase.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
            if (z2) {
                this.database.executeFast("CREATE TABLE paths(document_id INTEGER, dc_id INTEGER, type INTEGER, path TEXT, PRIMARY KEY(document_id, dc_id, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS path_in_paths ON paths(path);").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 2").stepThis().dispose();
            } else {
                int intValue = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current files db version = " + intValue);
                }
                if (intValue == 0) {
                    throw new Exception("malformed");
                }
                migrateDatabase(intValue);
            }
            if (!z) {
                createBackup();
            }
            FileLog.d("files db created from_backup= " + z);
        } catch (Exception e) {
            if (i < 4) {
                if (!z && restoreBackup()) {
                    createDatabase(i + 1, true);
                    return;
                }
                this.cacheFile.delete();
                this.shmCacheFile.delete();
                createDatabase(i + 1, false);
            }
            if (!BuildVars.DEBUG_VERSION) {
                return;
            }
            FileLog.e(e);
        }
    }

    private void migrateDatabase(int i) throws SQLiteException {
        if (i == 1) {
            this.database.executeFast("CREATE INDEX IF NOT EXISTS path_in_paths ON paths(path);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 2").stepThis().dispose();
        }
    }

    private void createBackup() {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File file = new File(filesDirFixed, "account" + this.currentAccount + "/");
            file.mkdirs();
            filesDirFixed = file;
        }
        File file2 = new File(filesDirFixed, "file_to_path_backup.db");
        try {
            AndroidUtilities.copyFile(this.cacheFile, file2);
            FileLog.d("file db backup created " + file2.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean restoreBackup() {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File file = new File(filesDirFixed, "account" + this.currentAccount + "/");
            file.mkdirs();
            filesDirFixed = file;
        }
        File file2 = new File(filesDirFixed, "file_to_path_backup.db");
        if (!file2.exists()) {
            return false;
        }
        try {
            return AndroidUtilities.copyFile(file2, this.cacheFile);
        } catch (IOException e) {
            FileLog.e(e);
            return false;
        }
    }

    public String getPath(final long j, final int i, final int i2, boolean z) {
        SQLiteException sQLiteException;
        String str;
        SQLiteCursor queryFinalized;
        if (z) {
            if (BuildVars.DEBUG_VERSION && this.dispatchQueue.getHandler() != null && Thread.currentThread() == this.dispatchQueue.getHandler().getLooper().getThread()) {
                throw new RuntimeException("Error, lead to infinity loop");
            }
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final String[] strArr = new String[1];
            System.currentTimeMillis();
            this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    FilePathDatabase.this.lambda$getPath$1(j, i, i2, strArr, countDownLatch);
                }
            });
            try {
                countDownLatch.await();
            } catch (Exception unused) {
            }
            return strArr[0];
        }
        SQLiteCursor sQLiteCursor = null;
        r1 = null;
        String str2 = null;
        sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized("SELECT path FROM paths WHERE document_id = " + j + " AND dc_id = " + i + " AND type = " + i2, new Object[0]);
            } catch (SQLiteException e) {
                sQLiteException = e;
                str = null;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            if (queryFinalized.next()) {
                str2 = queryFinalized.stringValue(0);
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("get file path id=" + j + " dc=" + i + " type=" + i2 + " path=" + str2);
                }
            }
            queryFinalized.dispose();
            return str2;
        } catch (SQLiteException e2) {
            sQLiteException = e2;
            str = str2;
            sQLiteCursor = queryFinalized;
            FileLog.e(sQLiteException);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            return str;
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0070, code lost:
        if (r0 == null) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getPath$1(long r6, int r8, int r9, java.lang.String[] r10, java.util.concurrent.CountDownLatch r11) {
        /*
            r5 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r5.database     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r2.<init>()     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r3 = "SELECT path FROM paths WHERE document_id = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r2.append(r6)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r3 = " AND dc_id = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r2.append(r8)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r3 = " AND type = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r2.append(r9)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r3 = 0
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r2, r4)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            boolean r1 = r0.next()     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            if (r1 == 0) goto L72
            java.lang.String r1 = r0.stringValue(r3)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r10[r3] = r1     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            if (r1 == 0) goto L72
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r1.<init>()     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r2 = "get file path id="
            r1.append(r2)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r1.append(r6)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r6 = " dc="
            r1.append(r6)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r1.append(r8)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r6 = " type="
            r1.append(r6)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r1.append(r9)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r6 = " path="
            r1.append(r6)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r6 = r10[r3]     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            r1.append(r6)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            java.lang.String r6 = r1.toString()     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            org.telegram.messenger.FileLog.d(r6)     // Catch: java.lang.Throwable -> L6a org.telegram.SQLite.SQLiteException -> L6c
            goto L72
        L6a:
            r6 = move-exception
            goto L79
        L6c:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> L6a
            if (r0 == 0) goto L75
        L72:
            r0.dispose()
        L75:
            r11.countDown()
            return
        L79:
            if (r0 == 0) goto L7e
            r0.dispose()
        L7e:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FilePathDatabase.lambda$getPath$1(long, int, int, java.lang.String[], java.util.concurrent.CountDownLatch):void");
    }

    public void putPath(final long j, final int i, final int i2, final String str) {
        this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                FilePathDatabase.this.lambda$putPath$2(j, i, i2, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00c2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putPath$2(long r6, int r8, int r9, java.lang.String r10) {
        /*
            r5 = this;
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L30
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "put file path id="
            r0.append(r1)
            r0.append(r6)
            java.lang.String r1 = " dc="
            r0.append(r1)
            r0.append(r8)
            java.lang.String r1 = " type="
            r0.append(r1)
            r0.append(r9)
            java.lang.String r1 = " path="
            r0.append(r1)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L30:
            r0 = 0
            if (r10 == 0) goto L6d
            org.telegram.SQLite.SQLiteDatabase r1 = r5.database     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            java.lang.String r2 = "DELETE FROM paths WHERE path = ?"
            org.telegram.SQLite.SQLitePreparedStatement r1 = r1.executeFast(r2)     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            r2 = 1
            r1.bindString(r2, r10)     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r1.step()     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            org.telegram.SQLite.SQLiteDatabase r3 = r5.database     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            java.lang.String r4 = "REPLACE INTO paths VALUES(?, ?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r3.executeFast(r4)     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r0.requery()     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r0.bindLong(r2, r6)     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r6 = 2
            r0.bindInteger(r6, r8)     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r6 = 3
            r0.bindInteger(r6, r9)     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r6 = 4
            r0.bindString(r6, r10)     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r0.step()     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r0.dispose()     // Catch: java.lang.Throwable -> L65 org.telegram.SQLite.SQLiteException -> L69
            r6 = r0
            r0 = r1
            goto L9c
        L65:
            r6 = move-exception
            r7 = r0
            r0 = r1
            goto Lbb
        L69:
            r6 = move-exception
            r7 = r0
            r0 = r1
            goto Lac
        L6d:
            org.telegram.SQLite.SQLiteDatabase r10 = r5.database     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            r1.<init>()     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            java.lang.String r2 = "DELETE FROM paths WHERE document_id = "
            r1.append(r2)     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            r1.append(r6)     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            java.lang.String r6 = " AND dc_id = "
            r1.append(r6)     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            r1.append(r8)     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            java.lang.String r6 = " AND type = "
            r1.append(r6)     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            r1.append(r9)     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            java.lang.String r6 = r1.toString()     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            org.telegram.SQLite.SQLitePreparedStatement r6 = r10.executeFast(r6)     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            r6.dispose()     // Catch: java.lang.Throwable -> La7 org.telegram.SQLite.SQLiteException -> Laa
            r6 = r0
        L9c:
            if (r0 == 0) goto La1
            r0.dispose()
        La1:
            if (r6 == 0) goto Lb9
            r6.dispose()
            goto Lb9
        La7:
            r6 = move-exception
            r7 = r0
            goto Lbb
        Laa:
            r6 = move-exception
            r7 = r0
        Lac:
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> Lba
            if (r0 == 0) goto Lb4
            r0.dispose()
        Lb4:
            if (r7 == 0) goto Lb9
            r7.dispose()
        Lb9:
            return
        Lba:
            r6 = move-exception
        Lbb:
            if (r0 == 0) goto Lc0
            r0.dispose()
        Lc0:
            if (r7 == 0) goto Lc5
            r7.dispose()
        Lc5:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FilePathDatabase.lambda$putPath$2(long, int, int, java.lang.String):void");
    }

    public void checkMediaExistance(ArrayList<MessageObject> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        final ArrayList arrayList2 = new ArrayList(arrayList);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        long currentTimeMillis = System.currentTimeMillis();
        this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FilePathDatabase.lambda$checkMediaExistance$3(arrayList2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            FileLog.e(e);
        }
        FileLog.d("checkMediaExistance size=" + arrayList.size() + " time=" + (System.currentTimeMillis() - currentTimeMillis));
        if (!BuildVars.DEBUG_VERSION || Thread.currentThread() != Looper.getMainLooper().getThread()) {
            return;
        }
        FileLog.e(new Exception("warning, not allowed in main thread"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkMediaExistance$3(ArrayList arrayList, CountDownLatch countDownLatch) {
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                ((MessageObject) arrayList.get(i)).checkMediaExistance(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
    }

    public void clear() {
        this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FilePathDatabase.this.lambda$clear$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clear$4() {
        try {
            this.database.executeFast("DELETE FROM paths WHERE 1").stepThis().dispose();
        } catch (SQLiteException e) {
            FileLog.e(e);
        }
    }

    public boolean hasAnotherRefOnFile(final String str) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = {false};
        this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                FilePathDatabase.this.lambda$hasAnotherRefOnFile$5(str, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hasAnotherRefOnFile$5(String str, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase.queryFinalized("SELECT document_id FROM paths WHERE path = '" + str + "'", new Object[0]).next()) {
                zArr[0] = true;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        countDownLatch.countDown();
    }

    /* loaded from: classes.dex */
    public static class PathData {
        public final int dc;
        public final long id;
        public final int type;

        public PathData(long j, int i, int i2) {
            this.id = j;
            this.dc = i;
            this.type = i2;
        }
    }
}
