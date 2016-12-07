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
public class zzanq implements ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private static final zzanq bkR = new zzanq();
    private final AtomicBoolean bkS = new AtomicBoolean();
    private boolean cR;

    private zzanq() {
    }

    public static zzanq N() {
        return bkR;
    }

    public static void zza(Application application) {
        application.registerActivityLifecycleCallbacks(bkR);
        application.registerComponentCallbacks(bkR);
        bkR.cR = true;
    }

    public boolean O() {
        return this.bkS.get();
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (this.bkS.compareAndSet(true, false)) {
            FirebaseApp.zzcr(false);
        }
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
        if (this.bkS.compareAndSet(true, false)) {
            FirebaseApp.zzcr(false);
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
        if (i == 20 && this.bkS.compareAndSet(false, true)) {
            FirebaseApp.zzcr(true);
        }
    }
}
