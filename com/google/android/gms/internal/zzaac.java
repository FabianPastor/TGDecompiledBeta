package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.gms.common.util.zzt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public final class zzaac implements ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private static final zzaac zzazV = new zzaac();
    private final ArrayList<zza> mListeners = new ArrayList();
    private boolean zzadP = false;
    private final AtomicBoolean zzazW = new AtomicBoolean();
    private final AtomicBoolean zzazX = new AtomicBoolean();

    public interface zza {
        void zzat(boolean z);
    }

    private zzaac() {
    }

    public static void zza(Application application) {
        synchronized (zzazV) {
            if (!zzazV.zzadP) {
                application.registerActivityLifecycleCallbacks(zzazV);
                application.registerComponentCallbacks(zzazV);
                zzazV.zzadP = true;
            }
        }
    }

    private void zzat(boolean z) {
        synchronized (zzazV) {
            Iterator it = this.mListeners.iterator();
            while (it.hasNext()) {
                ((zza) it.next()).zzat(z);
            }
        }
    }

    public static zzaac zzvB() {
        return zzazV;
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
        boolean compareAndSet = this.zzazW.compareAndSet(true, false);
        this.zzazX.set(true);
        if (compareAndSet) {
            zzat(false);
        }
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
        boolean compareAndSet = this.zzazW.compareAndSet(true, false);
        this.zzazX.set(true);
        if (compareAndSet) {
            zzat(false);
        }
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int i) {
        if (i == 20 && this.zzazW.compareAndSet(false, true)) {
            this.zzazX.set(true);
            zzat(true);
        }
    }

    public void zza(zza com_google_android_gms_internal_zzaac_zza) {
        synchronized (zzazV) {
            this.mListeners.add(com_google_android_gms_internal_zzaac_zza);
        }
    }

    @TargetApi(16)
    public boolean zzas(boolean z) {
        if (!this.zzazX.get()) {
            if (!zzt.zzzi()) {
                return z;
            }
            RunningAppProcessInfo runningAppProcessInfo = new RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(runningAppProcessInfo);
            if (!this.zzazX.getAndSet(true) && runningAppProcessInfo.importance > 100) {
                this.zzazW.set(true);
            }
        }
        return zzvC();
    }

    public boolean zzvC() {
        return this.zzazW.get();
    }
}
