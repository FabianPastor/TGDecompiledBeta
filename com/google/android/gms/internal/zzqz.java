package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzs;

public class zzqz {
    private final Object yX;

    public zzqz(Activity activity) {
        zzac.zzb((Object) activity, (Object) "Activity must not be null");
        boolean z = zzs.zzaxk() || (activity instanceof FragmentActivity);
        zzac.zzb(z, (Object) "This Activity is not supported before platform version 11 (3.0 Honeycomb). Please use FragmentActivity instead.");
        this.yX = activity;
    }

    public boolean zzasn() {
        return this.yX instanceof FragmentActivity;
    }

    public Activity zzaso() {
        return (Activity) this.yX;
    }

    public FragmentActivity zzasp() {
        return (FragmentActivity) this.yX;
    }
}
