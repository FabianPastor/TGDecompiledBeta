package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
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
                    if (keepMedia == 0) {
                        days = 7;
                    } else if (keepMedia == 1) {
                        days = 30;
                    } else {
                        days = 3;
                    }
                    long currentTime = (System.currentTimeMillis() / 1000) - ((long) (86400 * days));
                    SparseArray<File> paths = ImageLoader.getInstance().createMediaPaths();
                    for (int a = 0; a < paths.size(); a++) {
                        if (paths.keyAt(a) != 4) {
                            try {
                                Utilities.clearDir(((File) paths.valueAt(a)).getAbsolutePath(), 0, currentTime);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    }
                }
            });
        }
    }
}
