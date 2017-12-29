package com.google.android.gms.common.api.internal;

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

public final class zzk implements ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private static final zzk zzfog = new zzk();
    private boolean zzdtb = false;
    private final AtomicBoolean zzfoh = new AtomicBoolean();
    private final AtomicBoolean zzfoi = new AtomicBoolean();
    private final ArrayList<zzl> zzfoj = new ArrayList();

    private zzk() {
    }

    public static void zza(Application application) {
        synchronized (zzfog) {
            if (!zzfog.zzdtb) {
                application.registerActivityLifecycleCallbacks(zzfog);
                application.registerComponentCallbacks(zzfog);
                zzfog.zzdtb = true;
            }
        }
    }

    public static zzk zzahb() {
        return zzfog;
    }

    private final void zzbf(boolean z) {
        synchronized (zzfog) {
            ArrayList arrayList = this.zzfoj;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                ((zzl) obj).zzbf(z);
            }
        }
    }

    public final void onActivityCreated(Activity activity, Bundle bundle) {
        boolean compareAndSet = this.zzfoh.compareAndSet(true, false);
        this.zzfoi.set(true);
        if (compareAndSet) {
            zzbf(false);
        }
    }

    public final void onActivityDestroyed(Activity activity) {
    }

    public final void onActivityPaused(Activity activity) {
    }

    public final void onActivityResumed(Activity activity) {
        boolean compareAndSet = this.zzfoh.compareAndSet(true, false);
        this.zzfoi.set(true);
        if (compareAndSet) {
            zzbf(false);
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
        if (i == 20 && this.zzfoh.compareAndSet(false, true)) {
            this.zzfoi.set(true);
            zzbf(true);
        }
    }

    public final void zza(zzl com_google_android_gms_common_api_internal_zzl) {
        synchronized (zzfog) {
            this.zzfoj.add(com_google_android_gms_common_api_internal_zzl);
        }
    }

    @TargetApi(16)
    public final boolean zzbe(boolean z) {
        if (!this.zzfoi.get()) {
            if (!zzq.zzami()) {
                return true;
            }
            RunningAppProcessInfo runningAppProcessInfo = new RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(runningAppProcessInfo);
            if (!this.zzfoi.getAndSet(true) && runningAppProcessInfo.importance > 100) {
                this.zzfoh.set(true);
            }
        }
        return this.zzfoh.get();
    }
}
