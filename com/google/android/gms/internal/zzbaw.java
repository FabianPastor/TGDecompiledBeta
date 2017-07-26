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
import com.google.android.gms.common.util.zzq;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class zzbaw implements ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private static final zzbaw zzaBJ = new zzbaw();
    private final ArrayList<zzbax> mListeners = new ArrayList();
    private final AtomicBoolean zzaBK = new AtomicBoolean();
    private final AtomicBoolean zzaBL = new AtomicBoolean();
    private boolean zzafK = false;

    private zzbaw() {
    }

    public static void zza(Application application) {
        synchronized (zzaBJ) {
            if (!zzaBJ.zzafK) {
                application.registerActivityLifecycleCallbacks(zzaBJ);
                application.registerComponentCallbacks(zzaBJ);
                zzaBJ.zzafK = true;
            }
        }
    }

    private final void zzac(boolean z) {
        synchronized (zzaBJ) {
            ArrayList arrayList = this.mListeners;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                ((zzbax) obj).zzac(z);
            }
        }
    }

    public static zzbaw zzpv() {
        return zzaBJ;
    }

    public final void onActivityCreated(Activity activity, Bundle bundle) {
        boolean compareAndSet = this.zzaBK.compareAndSet(true, false);
        this.zzaBL.set(true);
        if (compareAndSet) {
            zzac(false);
        }
    }

    public final void onActivityDestroyed(Activity activity) {
    }

    public final void onActivityPaused(Activity activity) {
    }

    public final void onActivityResumed(Activity activity) {
        boolean compareAndSet = this.zzaBK.compareAndSet(true, false);
        this.zzaBL.set(true);
        if (compareAndSet) {
            zzac(false);
        }
    }

    public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public final void onActivityStarted(Activity activity) {
    }

    public final void onActivityStopped(Activity activity) {
    }

    public final void onConfigurationChanged(Configuration configuration) {
    }

    public final void onLowMemory() {
    }

    public final void onTrimMemory(int i) {
        if (i == 20 && this.zzaBK.compareAndSet(false, true)) {
            this.zzaBL.set(true);
            zzac(true);
        }
    }

    public final void zza(zzbax com_google_android_gms_internal_zzbax) {
        synchronized (zzaBJ) {
            this.mListeners.add(com_google_android_gms_internal_zzbax);
        }
    }

    @TargetApi(16)
    public final boolean zzab(boolean z) {
        if (!this.zzaBL.get()) {
            if (!zzq.zzrZ()) {
                return true;
            }
            RunningAppProcessInfo runningAppProcessInfo = new RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(runningAppProcessInfo);
            if (!this.zzaBL.getAndSet(true) && runningAppProcessInfo.importance > 100) {
                this.zzaBK.set(true);
            }
        }
        return this.zzaBK.get();
    }

    public final boolean zzpw() {
        return this.zzaBK.get();
    }
}
