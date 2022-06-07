package org.telegram.messenger;

import android.os.Looper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;

public class FilePathDatabase {
    private static final String DATABASE_BACKUP_NAME = "file_to_path_backup";
    private static final String DATABASE_NAME = "file_to_path";
    private static final int LAST_DB_VERSION = 1;
    private File cacheFile;
    private final int currentAccount;
    private SQLiteDatabase database;
    private final DispatchQueue dispatchQueue;
    private File shmCacheFile;
    private File walCacheFile;

    public FilePathDatabase(int i) {
        this.currentAccount = i;
        DispatchQueue dispatchQueue2 = new DispatchQueue("files_database_queue_" + i);
        this.dispatchQueue = dispatchQueue2;
        dispatchQueue2.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        createDatabase(false);
    }

    public void createDatabase(boolean z) {
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
                this.database.executeFast("PRAGMA user_version = 1").stepThis().dispose();
            }
            if (!z) {
                createBackup();
            }
            FileLog.d("files db created from_backup= " + z);
        } catch (Exception e) {
            if (!z && restoreBackup()) {
                createDatabase(true);
            } else if (BuildVars.DEBUG_VERSION) {
                throw new RuntimeException(e);
            }
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
            e.printStackTrace();
            return false;
        }
    }

    public String getPath(long j, int i, int i2, boolean z) {
        if (!z) {
            String str = null;
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT path FROM paths WHERE document_id = " + j + " AND dc_id = " + i + " AND type = " + i2, new Object[0]);
                if (queryFinalized.next()) {
                    str = queryFinalized.stringValue(0);
                }
                queryFinalized.dispose();
            } catch (SQLiteException e) {
                if (BuildVars.DEBUG_VERSION) {
                    throw new RuntimeException(e);
                }
            }
            return str;
        } else if (!BuildVars.DEBUG_VERSION || this.dispatchQueue.getHandler() == null || Thread.currentThread() != this.dispatchQueue.getHandler().getLooper().getThread()) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            String[] strArr = new String[1];
            System.currentTimeMillis();
            this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda2(this, j, i, i2, strArr, countDownLatch));
            try {
                countDownLatch.await();
            } catch (Exception unused) {
            }
            return strArr[0];
        } else {
            throw new RuntimeException("Error, lead to infinity loop");
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getPath$1(long j, int i, int i2, String[] strArr, CountDownLatch countDownLatch) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT path FROM paths WHERE document_id = " + j + " AND dc_id = " + i + " AND type = " + i2, new Object[0]);
            if (queryFinalized.next()) {
                strArr[0] = queryFinalized.stringValue(0);
            }
            queryFinalized.dispose();
            countDownLatch.countDown();
        } catch (SQLiteException e) {
            throw new RuntimeException(e);
        }
    }

    public void putPath(long j, int i, int i2, String str) {
        this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda3(this, str, j, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putPath$2(String str, long j, int i, int i2) {
        if (str != null) {
            try {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO paths VALUES(?, ?, ?, ?)");
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, i);
                executeFast.bindInteger(3, i2);
                executeFast.bindString(4, str);
                executeFast.step();
            } catch (SQLiteException e) {
                if (BuildVars.DEBUG_VERSION) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM paths WHERE document_id = " + j + " AND dc_id = " + i + " AND type = " + i2).stepThis().dispose();
        }
    }

    public void checkMediaExistance(ArrayList<MessageObject> arrayList) {
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList(arrayList);
            CountDownLatch countDownLatch = new CountDownLatch(1);
            long currentTimeMillis = System.currentTimeMillis();
            this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda0(arrayList2, countDownLatch));
            try {
                countDownLatch.await();
                FileLog.d("checkMediaExistance size=" + arrayList.size() + " time=" + (System.currentTimeMillis() - currentTimeMillis));
                if (BuildVars.DEBUG_VERSION && Thread.currentThread() == Looper.getMainLooper().getThread()) {
                    FileLog.e((Throwable) new Exception("warning, not allowed in main thread"));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
