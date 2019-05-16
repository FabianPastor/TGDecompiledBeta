package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build.VERSION;
import android.os.Bundle;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

@SuppressLint({"NewApi"})
public class ForegroundDetector implements ActivityLifecycleCallbacks {
    private static ForegroundDetector Instance;
    private long enterBackgroundTime = 0;
    private CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList();
    private int refs;
    private boolean wasInBackground = true;

    public interface Listener {
        void onBecameBackground();

        void onBecameForeground();
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public static ForegroundDetector getInstance() {
        return Instance;
    }

    public ForegroundDetector(Application application) {
        Instance = this;
        application.registerActivityLifecycleCallbacks(this);
    }

    public boolean isForeground() {
        return this.refs > 0;
    }

    public boolean isBackground() {
        return this.refs == 0;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void onActivityStarted(Activity activity) {
        int i = this.refs + 1;
        this.refs = i;
        if (i == 1) {
            if (System.currentTimeMillis() - this.enterBackgroundTime < 200) {
                this.wasInBackground = false;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("switch to foreground");
            }
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                try {
                    ((Listener) it.next()).onBecameForeground();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public boolean isWasInBackground(boolean z) {
        if (z && VERSION.SDK_INT >= 21 && System.currentTimeMillis() - this.enterBackgroundTime < 200) {
            this.wasInBackground = false;
        }
        return this.wasInBackground;
    }

    public void resetBackgroundVar() {
        this.wasInBackground = false;
    }

    public void onActivityStopped(Activity activity) {
        int i = this.refs - 1;
        this.refs = i;
        if (i == 0) {
            this.enterBackgroundTime = System.currentTimeMillis();
            this.wasInBackground = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("switch to background");
            }
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                try {
                    ((Listener) it.next()).onBecameBackground();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }
}
