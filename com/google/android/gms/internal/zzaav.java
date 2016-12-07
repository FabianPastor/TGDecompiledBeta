package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzs;

public class zzaav {
    private final Object zzaBr;

    public zzaav(Activity activity) {
        zzac.zzb((Object) activity, (Object) "Activity must not be null");
        boolean z = zzs.zzyx() || (activity instanceof FragmentActivity);
        zzac.zzb(z, (Object) "This Activity is not supported before platform version 11 (3.0 Honeycomb). Please use FragmentActivity instead.");
        this.zzaBr = activity;
    }

    public boolean zzwl() {
        return this.zzaBr instanceof FragmentActivity;
    }

    public Activity zzwm() {
        return (Activity) this.zzaBr;
    }

    public FragmentActivity zzwn() {
        return (FragmentActivity) this.zzaBr;
    }
}
