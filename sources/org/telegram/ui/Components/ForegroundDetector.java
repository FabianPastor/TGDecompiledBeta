package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build.VERSION;
import android.os.Bundle;
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
        activity = this.refs + 1;
        this.refs = activity;
        if (activity == 1) {
            if (System.currentTimeMillis() - this.enterBackgroundTime < 200) {
                this.wasInBackground = null;
            }
            if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m0d("switch to foreground");
            }
            activity = this.listeners.iterator();
            while (activity.hasNext()) {
                try {
                    ((Listener) activity.next()).onBecameForeground();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }

    public boolean isWasInBackground(boolean z) {
        if (z && VERSION.SDK_INT >= true && System.currentTimeMillis() - this.enterBackgroundTime < 200) {
            this.wasInBackground = false;
        }
        return this.wasInBackground;
    }

    public void resetBackgroundVar() {
        this.wasInBackground = false;
    }

    public void onActivityStopped(Activity activity) {
        activity = this.refs - 1;
        this.refs = activity;
        if (activity == null) {
            this.enterBackgroundTime = System.currentTimeMillis();
            this.wasInBackground = true;
            if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m0d("switch to background");
            }
            activity = this.listeners.iterator();
            while (activity.hasNext()) {
                try {
                    ((Listener) activity.next()).onBecameBackground();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }
}
