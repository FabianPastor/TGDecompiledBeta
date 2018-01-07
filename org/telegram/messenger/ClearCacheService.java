package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build.VERSION;
import android.system.Os;
import android.system.StructStat;
import android.util.SparseArray;
import java.io.File;

public class ClearCacheService extends IntentService {
    public ClearCacheService() {
        super("ClearCacheService");
    }

    protected void onHandleIntent(Intent intent) {
        ApplicationLoader.postInitApplication();
        final int keepMedia = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
        if (keepMedia != 2) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public void run() {
                    int days;
                    long currentTime = System.currentTimeMillis();
                    if (keepMedia == 0) {
                        days = 7;
                    } else if (keepMedia == 1) {
                        days = 30;
                    } else {
                        days = 3;
                    }
                    long diff = (long) (86400000 * days);
                    SparseArray<File> paths = ImageLoader.getInstance().createMediaPaths();
                    for (int a = 0; a < paths.size(); a++) {
                        if (paths.keyAt(a) != 4) {
                            File[] array = ((File) paths.valueAt(a)).listFiles();
                            if (array != null) {
                                for (File f : array) {
                                    if (f.isFile() && !f.getName().equals(".nomedia")) {
                                        if (VERSION.SDK_INT >= 21) {
                                            try {
                                                StructStat stat = Os.stat(f.getPath());
                                                if (stat.st_atime != 0) {
                                                    if (stat.st_atime + diff < currentTime) {
                                                        f.delete();
                                                    }
                                                } else if (stat.st_mtime + diff < currentTime) {
                                                    f.delete();
                                                }
                                            } catch (Throwable e) {
                                                FileLog.e(e);
                                            } catch (Throwable e2) {
                                                FileLog.e(e2);
                                            }
                                        } else if (f.lastModified() + diff < currentTime) {
                                            f.delete();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}
