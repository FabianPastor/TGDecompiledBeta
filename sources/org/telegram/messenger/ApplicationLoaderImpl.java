package org.telegram.messenger;

import android.app.Activity;
import android.os.SystemClock;
import android.text.TextUtils;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.distribute.Distribute;

public class ApplicationLoaderImpl extends ApplicationLoader {
    private static long lastUpdateCheckTime;

    /* access modifiers changed from: protected */
    public String onGetApplicationId() {
        return "org.telegram.messenger.beta";
    }

    /* access modifiers changed from: protected */
    public void startAppCenterInternal(Activity activity) {
        try {
            if (BuildVars.DEBUG_VERSION) {
                Distribute.setEnabledForDebuggableBuild(true);
                if (!TextUtils.isEmpty("var_-67c9-48d2-b5d0-4761f1c1a8f3")) {
                    AppCenter.start(activity.getApplication(), "var_-67c9-48d2-b5d0-4761f1c1a8f3", Distribute.class, Crashes.class);
                    AppCenter.setUserId("uid=" + UserConfig.getInstance(UserConfig.selectedAccount).clientUserId);
                    return;
                }
                throw new RuntimeException("App Center hash is empty. add to local.properties field APP_CENTER_HASH_PRIVATE and APP_CENTER_HASH_PUBLIC");
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* access modifiers changed from: protected */
    public void checkForUpdatesInternal() {
        try {
            if (BuildVars.DEBUG_VERSION && SystemClock.elapsedRealtime() - lastUpdateCheckTime >= 3600000) {
                lastUpdateCheckTime = SystemClock.elapsedRealtime();
                Distribute.checkForUpdate();
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* access modifiers changed from: protected */
    public void appCenterLogInternal(Throwable th) {
        try {
            Crashes.trackError(th);
        } catch (Throwable unused) {
        }
    }
}
