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
        intent = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
        if (intent != 2) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public void run() {
                    int i = intent == 0 ? 7 : intent == 1 ? 30 : 3;
                    long currentTimeMillis = (System.currentTimeMillis() / 1000) - ((long) (86400 * i));
                    SparseArray createMediaPaths = ImageLoader.getInstance().createMediaPaths();
                    for (int i2 = 0; i2 < createMediaPaths.size(); i2++) {
                        if (createMediaPaths.keyAt(i2) != 4) {
                            try {
                                Utilities.clearDir(((File) createMediaPaths.valueAt(i2)).getAbsolutePath(), 0, currentTimeMillis);
                            } catch (Throwable th) {
                                FileLog.m3e(th);
                            }
                        }
                    }
                }
            });
        }
    }
}
