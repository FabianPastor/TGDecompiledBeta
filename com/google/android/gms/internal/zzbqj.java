package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;
import java.util.concurrent.atomic.AtomicBoolean;

@TargetApi(14)
public class zzbqj implements ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private static final zzbqj zzcjk = new zzbqj();
    private boolean zzacO;
    private final AtomicBoolean zzcjl = new AtomicBoolean();

    private zzbqj() {
    }

    public static void zza(Application application) {
        application.registerActivityLifecycleCallbacks(zzcjk);
        application.registerComponentCallbacks(zzcjk);
        zzcjk.zzacO = true;
    }

    public static zzbqj zzaan() {
        return zzcjk;
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (this.zzcjl.compareAndSet(true, false)) {
            FirebaseApp.zzaQ(false);
        }
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
        if (this.zzcjl.compareAndSet(true, false)) {
            FirebaseApp.zzaQ(false);
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
        if (i == 20 && this.zzcjl.compareAndSet(false, true)) {
            FirebaseApp.zzaQ(true);
        }
    }

    public boolean zzaao() {
        return this.zzcjl.get();
    }
}
