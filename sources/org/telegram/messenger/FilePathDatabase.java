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

    public FilePathDatabase(int currentAccount2) {
        this.currentAccount = currentAccount2;
        DispatchQueue dispatchQueue2 = new DispatchQueue("files_database_queue_" + currentAccount2);
        this.dispatchQueue = dispatchQueue2;
        dispatchQueue2.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda1(this));
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-FilePathDatabase  reason: not valid java name */
    public /* synthetic */ void m1832lambda$new$0$orgtelegrammessengerFilePathDatabase() {
        createDatabase(0, false);
    }

    public void createDatabase(int tryCount, boolean fromBackup) {
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            filesDir = new File(filesDir, "account" + this.currentAccount + "/");
            filesDir.mkdirs();
        }
        this.cacheFile = new File(filesDir, "file_to_path.db");
        this.shmCacheFile = new File(filesDir, "file_to_path.db-shm");
        boolean createTable = false;
        if (!this.cacheFile.exists()) {
            createTable = true;
        }
        try {
            SQLiteDatabase sQLiteDatabase = new SQLiteDatabase(this.cacheFile.getPath());
            this.database = sQLiteDatabase;
            sQLiteDatabase.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
            if (createTable) {
                this.database.executeFast("CREATE TABLE paths(document_id INTEGER, dc_id INTEGER, type INTEGER, path TEXT, PRIMARY KEY(document_id, dc_id, type));").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 1").stepThis().dispose();
            } else {
                int version = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current files db version = " + version);
                }
                if (version == 0) {
                    throw new Exception("malformed");
                }
            }
            if (!fromBackup) {
                createBackup();
            }
            FileLog.d("files db created from_backup= " + fromBackup);
        } catch (Exception e) {
            if (tryCount < 4) {
                if (fromBackup || !restoreBackup()) {
                    this.cacheFile.delete();
                    this.shmCacheFile.delete();
                    createDatabase(tryCount + 1, false);
                } else {
                    createDatabase(tryCount + 1, true);
                    return;
                }
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private void createBackup() {
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            filesDir = new File(filesDir, "account" + this.currentAccount + "/");
            filesDir.mkdirs();
        }
        File backupCacheFile = new File(filesDir, "file_to_path_backup.db");
        try {
            AndroidUtilities.copyFile(this.cacheFile, backupCacheFile);
            FileLog.d("file db backup created " + backupCacheFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean restoreBackup() {
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            filesDir = new File(filesDir, "account" + this.currentAccount + "/");
            filesDir.mkdirs();
        }
        File backupCacheFile = new File(filesDir, "file_to_path_backup.db");
        if (!backupCacheFile.exists()) {
            return false;
        }
        try {
            return AndroidUtilities.copyFile(backupCacheFile, this.cacheFile);
        } catch (IOException e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public String getPath(long documentId, int dc, int type, boolean useQueue) {
        long j = documentId;
        int i = dc;
        int i2 = type;
        if (!useQueue) {
            String res = null;
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT path FROM paths WHERE document_id = " + j + " AND dc_id = " + i + " AND type = " + i2, new Object[0]);
                if (cursor.next()) {
                    res = cursor.stringValue(0);
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.d("get file path id=" + j + " dc=" + i + " type=" + i2 + " path=" + res);
                    }
                }
                cursor.dispose();
            } catch (SQLiteException e) {
                FileLog.e((Throwable) e);
            }
            return res;
        } else if (!BuildVars.DEBUG_VERSION || this.dispatchQueue.getHandler() == null || Thread.currentThread() != this.dispatchQueue.getHandler().getLooper().getThread()) {
            CountDownLatch syncLatch = new CountDownLatch(1);
            String[] res2 = new String[1];
            long currentTimeMillis = System.currentTimeMillis();
            DispatchQueue dispatchQueue2 = this.dispatchQueue;
            FilePathDatabase$$ExternalSyntheticLambda3 filePathDatabase$$ExternalSyntheticLambda3 = r1;
            FilePathDatabase$$ExternalSyntheticLambda3 filePathDatabase$$ExternalSyntheticLambda32 = new FilePathDatabase$$ExternalSyntheticLambda3(this, documentId, dc, type, res2, syncLatch);
            dispatchQueue2.postRunnable(filePathDatabase$$ExternalSyntheticLambda3);
            try {
                syncLatch.await();
            } catch (Exception e2) {
            }
            return res2[0];
        } else {
            throw new RuntimeException("Error, lead to infinity loop");
        }
    }

    /* renamed from: lambda$getPath$1$org-telegram-messenger-FilePathDatabase  reason: not valid java name */
    public /* synthetic */ void m1831lambda$getPath$1$orgtelegrammessengerFilePathDatabase(long documentId, int dc, int type, String[] res, CountDownLatch syncLatch) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT path FROM paths WHERE document_id = " + documentId + " AND dc_id = " + dc + " AND type = " + type, new Object[0]);
            if (cursor.next()) {
                res[0] = cursor.stringValue(0);
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("get file path id=" + documentId + " dc=" + dc + " type=" + type + " path=" + res[0]);
                }
            }
            cursor.dispose();
        } catch (SQLiteException e) {
            FileLog.e((Throwable) e);
        }
        syncLatch.countDown();
    }

    public void putPath(long id, int dc, int type, String path) {
        this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda2(this, id, dc, type, path));
    }

    /* renamed from: lambda$putPath$2$org-telegram-messenger-FilePathDatabase  reason: not valid java name */
    public /* synthetic */ void m1833lambda$putPath$2$orgtelegrammessengerFilePathDatabase(long id, int dc, int type, String path) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("put file path id=" + id + " dc=" + dc + " type=" + type + " path=" + path);
        }
        if (path != null) {
            try {
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO paths VALUES(?, ?, ?, ?)");
                state.requery();
                state.bindLong(1, id);
                state.bindInteger(2, dc);
                state.bindInteger(3, type);
                state.bindString(4, path);
                state.step();
            } catch (SQLiteException e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM paths WHERE document_id = " + id + " AND dc_id = " + dc + " AND type = " + type).stepThis().dispose();
        }
    }

    public void checkMediaExistance(ArrayList<MessageObject> messageObjects) {
        if (!messageObjects.isEmpty()) {
            ArrayList<MessageObject> arrayListFinal = new ArrayList<>(messageObjects);
            CountDownLatch syncLatch = new CountDownLatch(1);
            long time = System.currentTimeMillis();
            this.dispatchQueue.postRunnable(new FilePathDatabase$$ExternalSyntheticLambda0(arrayListFinal, syncLatch));
            try {
                syncLatch.await();
            } catch (InterruptedException e) {
                FileLog.e((Throwable) e);
            }
            FileLog.d("checkMediaExistance size=" + messageObjects.size() + " time=" + (System.currentTimeMillis() - time));
            if (BuildVars.DEBUG_VERSION && Thread.currentThread() == Looper.getMainLooper().getThread()) {
                FileLog.e((Throwable) new Exception("warning, not allowed in main thread"));
            }
        }
    }

    static /* synthetic */ void lambda$checkMediaExistance$3(ArrayList arrayListFinal, CountDownLatch syncLatch) {
        int i = 0;
        while (i < arrayListFinal.size()) {
            try {
                ((MessageObject) arrayListFinal.get(i)).checkMediaExistance(false);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        syncLatch.countDown();
    }

    public static class PathData {
        public final int dc;
        public final long id;
        public final int type;

        public PathData(long documentId, int dcId, int type2) {
            this.id = documentId;
            this.dc = dcId;
            this.type = type2;
        }
    }
}
