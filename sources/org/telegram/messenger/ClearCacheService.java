package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import android.util.SparseArray;
import java.io.File;

public class ClearCacheService extends IntentService {
    public ClearCacheService() {
        super("ClearCacheService");
    }

    /* Access modifiers changed, original: protected */
    public void onHandleIntent(Intent intent) {
        ApplicationLoader.postInitApplication();
        int i = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
        if (i != 2) {
            Utilities.globalQueue.postRunnable(new -$$Lambda$ClearCacheService$eaqMxt0ELhhnRq-8qc8rSbdFYt0(i));
        }
    }

    static /* synthetic */ void lambda$onHandleIntent$0(int i) {
        i = i == 0 ? 7 : i == 1 ? 30 : 3;
        long currentTimeMillis = (System.currentTimeMillis() / 1000) - ((long) (i * 86400));
        SparseArray createMediaPaths = ImageLoader.getInstance().createMediaPaths();
        for (int i2 = 0; i2 < createMediaPaths.size(); i2++) {
            if (createMediaPaths.keyAt(i2) != 4) {
                try {
                    Utilities.clearDir(((File) createMediaPaths.valueAt(i2)).getAbsolutePath(), 0, currentTimeMillis);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
    }
}
