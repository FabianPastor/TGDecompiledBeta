package org.telegram.messenger;

import android.os.Looper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;

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
        DispatchQueue dispatchQueue2 = new DispatchQueue("files_database_queue_" + i);
        this.dispatchQueue = dispatchQueue2;
        dispatchQueue2.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
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
                if (intValue != 0) {
                    migrateDatabase(intValue);
                } else {
                    throw new Exception("malformed");
                }
            }
            if (!z) {
                createBackup();
            }
            FileLog.d("files db created from_backup= " + z);
        } catch (Exception e) {
            if (i < 4) {
                if (z || !restoreBackup()) {
                    this.cacheFile.delete();
                    this.shmCacheFile.delete();
                    createDatabase(i + 1, false);
                } else {
                    createDatabase(i + 1, true);
                    return;
                }
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.e((Throwable) e);
            }
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
            FileLog.e((Throwable) e);
            return false;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r1v0 */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* JADX WARNING: type inference failed for: r1v16 */
    /* JADX WARNING: type inference failed for: r1v17 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00dc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getPath(long r17, int r19, int r20, boolean r21) {
        /*
            r16 = this;
            r9 = r16
            r3 = r17
            r0 = r19
            r6 = r20
            r10 = 0
            if (r21 == 0) goto L_0x005a
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r1 == 0) goto L_0x0034
            org.telegram.messenger.DispatchQueue r1 = r9.dispatchQueue
            android.os.Handler r1 = r1.getHandler()
            if (r1 == 0) goto L_0x0034
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            org.telegram.messenger.DispatchQueue r2 = r9.dispatchQueue
            android.os.Handler r2 = r2.getHandler()
            android.os.Looper r2 = r2.getLooper()
            java.lang.Thread r2 = r2.getThread()
            if (r1 == r2) goto L_0x002c
            goto L_0x0034
        L_0x002c:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "Error, lead to infinity loop"
            r0.<init>(r1)
            throw r0
        L_0x0034:
            java.util.concurrent.CountDownLatch r11 = new java.util.concurrent.CountDownLatch
            r1 = 1
            r11.<init>(r1)
            java.lang.String[] r12 = new java.lang.String[r1]
            java.lang.System.currentTimeMillis()
            org.telegram.messenger.DispatchQueue r13 = r9.dispatchQueue
            org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda4 r14 = new org.telegram.messenger.FilePathDatabase$$ExternalSyntheticLambda4
            r1 = r14
            r2 = r16
            r3 = r17
            r5 = r19
            r6 = r20
            r7 = r12
            r8 = r11
            r1.<init>(r2, r3, r5, r6, r7, r8)
            r13.postRunnable(r14)
            r11.await()     // Catch:{ Exception -> 0x0057 }
        L_0x0057:
            r0 = r12[r10]
            return r0
        L_0x005a:
            r1 = 0
            org.telegram.SQLite.SQLiteDatabase r2 = r9.database     // Catch:{ SQLiteException -> 0x00cd }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x00cd }
            r5.<init>()     // Catch:{ SQLiteException -> 0x00cd }
            java.lang.String r7 = "SELECT path FROM paths WHERE document_id = "
            r5.append(r7)     // Catch:{ SQLiteException -> 0x00cd }
            r5.append(r3)     // Catch:{ SQLiteException -> 0x00cd }
            java.lang.String r7 = " AND dc_id = "
            r5.append(r7)     // Catch:{ SQLiteException -> 0x00cd }
            r5.append(r0)     // Catch:{ SQLiteException -> 0x00cd }
            java.lang.String r7 = " AND type = "
            r5.append(r7)     // Catch:{ SQLiteException -> 0x00cd }
            r5.append(r6)     // Catch:{ SQLiteException -> 0x00cd }
            java.lang.String r5 = r5.toString()     // Catch:{ SQLiteException -> 0x00cd }
            java.lang.Object[] r7 = new java.lang.Object[r10]     // Catch:{ SQLiteException -> 0x00cd }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r5, r7)     // Catch:{ SQLiteException -> 0x00cd }
            boolean r5 = r2.next()     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            if (r5 == 0) goto L_0x00be
            java.lang.String r1 = r2.stringValue(r10)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            boolean r5 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            if (r5 == 0) goto L_0x00be
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            r5.<init>()     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            java.lang.String r7 = "get file path id="
            r5.append(r7)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            r5.append(r3)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            java.lang.String r3 = " dc="
            r5.append(r3)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            r5.append(r0)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            java.lang.String r0 = " type="
            r5.append(r0)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            r5.append(r6)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            java.lang.String r0 = " path="
            r5.append(r0)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            r5.append(r1)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            java.lang.String r0 = r5.toString()     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ SQLiteException -> 0x00c5, all -> 0x00c2 }
        L_0x00be:
            r2.dispose()
            goto L_0x00d9
        L_0x00c2:
            r0 = move-exception
            r1 = r2
            goto L_0x00da
        L_0x00c5:
            r0 = move-exception
            r15 = r2
            r2 = r0
            r0 = r1
            r1 = r15
            goto L_0x00d0
        L_0x00cb:
            r0 = move-exception
            goto L_0x00da
        L_0x00cd:
            r0 = move-exception
            r2 = r0
            r0 = r1
        L_0x00d0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x00cb }
            if (r1 == 0) goto L_0x00d8
            r1.dispose()
        L_0x00d8:
            r1 = r0
        L_0x00d9:
            return r1
        L_0x00da:
            if (r1 == 0) goto L_0x00df
            r1.dispose()
        L_0x00df:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FilePathDatabase.getPath(long, int, int, boolean):java.lang.String");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0070, code lost:
        if (r0 == null) goto L_0x0075;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$getPath$1(long r6, int r8, int r9, java.lang.String[] r10, java.util.concurrent.CountDownLatch r11) {
        /*
            r5 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r5.database     // Catch:{ SQLiteException -> 0x006c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x006c }
            r2.<init>()     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r3 = "SELECT path FROM paths WHERE document_id = "
            r2.append(r3)     // Catch:{ SQLiteException -> 0x006c }
            r2.append(r6)     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r3 = " AND dc_id = "
            r2.append(r3)     // Catch:{ SQLiteException -> 0x006c }
            r2.append(r8)     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r3 = " AND type = "
            r2.append(r3)     // Catch:{ SQLiteException -> 0x006c }
            r2.append(r9)     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r2 = r2.toString()     // Catch:{ SQLiteException -> 0x006c }
            r3 = 0
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ SQLiteException -> 0x006c }
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r2, r4)     // Catch:{ SQLiteException -> 0x006c }
            boolean r1 = r0.next()     // Catch:{ SQLiteException -> 0x006c }
            if (r1 == 0) goto L_0x0072
            java.lang.String r1 = r0.stringValue(r3)     // Catch:{ SQLiteException -> 0x006c }
            r10[r3] = r1     // Catch:{ SQLiteException -> 0x006c }
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ SQLiteException -> 0x006c }
            if (r1 == 0) goto L_0x0072
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x006c }
            r1.<init>()     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r2 = "get file path id="
            r1.append(r2)     // Catch:{ SQLiteException -> 0x006c }
            r1.append(r6)     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r6 = " dc="
            r1.append(r6)     // Catch:{ SQLiteException -> 0x006c }
            r1.append(r8)     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r6 = " type="
            r1.append(r6)     // Catch:{ SQLiteException -> 0x006c }
            r1.append(r9)     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r6 = " path="
            r1.append(r6)     // Catch:{ SQLiteException -> 0x006c }
            r6 = r10[r3]     // Catch:{ SQLiteException -> 0x006c }
            r1.append(r6)     // Catch:{ SQLiteException -> 0x006c }
            java.lang.String r6 = r1.toString()     // Catch:{ SQLiteException -> 0x006c }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ SQLiteException -> 0x006c }
            goto L_0x0072
        L_0x006a:
            r6 = move-exception
            goto L_0x0079
        L_0x006c:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x006a }
            if (r0 == 0) goto L_0x0075
        L_0x0072:
            r0.dispose()
        L_0x0075:
            r11.countDown()
            return
        L_0x0079:
            if (r0 == 0) goto L_0x007e
            r0.dispose()
        L_0x007e:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FilePathDatabase.lambda$getPath$1(long, int, int, java.lang.String[], java.util.concurrent.CountDownLatch):void");
    }

    public void putPath(long j, int i, int i2, String str) {
        this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda3(this, j, i, i2, str));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00bd  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$putPath$2(long r6, int r8, int r9, java.lang.String r10) {
        /*
            r5 = this;
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0030
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
        L_0x0030:
            r0 = 0
            if (r10 == 0) goto L_0x006d
            org.telegram.SQLite.SQLiteDatabase r1 = r5.database     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            java.lang.String r2 = "DELETE FROM paths WHERE path = ?"
            org.telegram.SQLite.SQLitePreparedStatement r1 = r1.executeFast(r2)     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            r2 = 1
            r1.bindString(r2, r10)     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r1.step()     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            org.telegram.SQLite.SQLiteDatabase r3 = r5.database     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            java.lang.String r4 = "REPLACE INTO paths VALUES(?, ?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r3.executeFast(r4)     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r0.requery()     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r0.bindLong(r2, r6)     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r6 = 2
            r0.bindInteger(r6, r8)     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r6 = 3
            r0.bindInteger(r6, r9)     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r6 = 4
            r0.bindString(r6, r10)     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r0.step()     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r0.dispose()     // Catch:{ SQLiteException -> 0x0069, all -> 0x0065 }
            r6 = r0
            r0 = r1
            goto L_0x009c
        L_0x0065:
            r6 = move-exception
            r7 = r0
            r0 = r1
            goto L_0x00bb
        L_0x0069:
            r6 = move-exception
            r7 = r0
            r0 = r1
            goto L_0x00ac
        L_0x006d:
            org.telegram.SQLite.SQLiteDatabase r10 = r5.database     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            r1.<init>()     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            java.lang.String r2 = "DELETE FROM paths WHERE document_id = "
            r1.append(r2)     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            r1.append(r6)     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            java.lang.String r6 = " AND dc_id = "
            r1.append(r6)     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            r1.append(r8)     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            java.lang.String r6 = " AND type = "
            r1.append(r6)     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            r1.append(r9)     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            java.lang.String r6 = r1.toString()     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r10.executeFast(r6)     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            r6.dispose()     // Catch:{ SQLiteException -> 0x00aa, all -> 0x00a7 }
            r6 = r0
        L_0x009c:
            if (r0 == 0) goto L_0x00a1
            r0.dispose()
        L_0x00a1:
            if (r6 == 0) goto L_0x00b9
            r6.dispose()
            goto L_0x00b9
        L_0x00a7:
            r6 = move-exception
            r7 = r0
            goto L_0x00bb
        L_0x00aa:
            r6 = move-exception
            r7 = r0
        L_0x00ac:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x00ba }
            if (r0 == 0) goto L_0x00b4
            r0.dispose()
        L_0x00b4:
            if (r7 == 0) goto L_0x00b9
            r7.dispose()
        L_0x00b9:
            return
        L_0x00ba:
            r6 = move-exception
        L_0x00bb:
            if (r0 == 0) goto L_0x00c0
            r0.dispose()
        L_0x00c0:
            if (r7 == 0) goto L_0x00c5
            r7.dispose()
        L_0x00c5:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FilePathDatabase.lambda$putPath$2(long, int, int, java.lang.String):void");
    }

    public void checkMediaExistance(ArrayList<MessageObject> arrayList) {
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList(arrayList);
            CountDownLatch countDownLatch = new CountDownLatch(1);
            long currentTimeMillis = System.currentTimeMillis();
            this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda0(arrayList2, countDownLatch));
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                FileLog.e((Throwable) e);
            }
            FileLog.d("checkMediaExistance size=" + arrayList.size() + " time=" + (System.currentTimeMillis() - currentTimeMillis));
            if (BuildVars.DEBUG_VERSION && Thread.currentThread() == Looper.getMainLooper().getThread()) {
                FileLog.e((Throwable) new Exception("warning, not allowed in main thread"));
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkMediaExistance$3(ArrayList arrayList, CountDownLatch countDownLatch) {
        int i = 0;
        while (i < arrayList.size()) {
            try {
                ((MessageObject) arrayList.get(i)).checkMediaExistance(false);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
    }

    public void clear() {
        this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clear$4() {
        try {
            this.database.executeFast("DELETE FROM paths WHERE 1").stepThis().dispose();
        } catch (SQLiteException e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean hasAnotherRefOnFile(String str) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = {false};
        this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda5(this, str, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            FileLog.e((Throwable) e);
        }
        return zArr[0];
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hasAnotherRefOnFile$5(String str, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase.queryFinalized("SELECT document_id FROM paths WHERE path = '" + str + "'", new Object[0]).next()) {
                zArr[0] = true;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        countDownLatch.countDown();
    }

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
