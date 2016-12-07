package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zzs;

public class zzrn {
    private final Object Be;

    public zzrn(Activity activity) {
        zzaa.zzb((Object) activity, (Object) "Activity must not be null");
        boolean z = zzs.zzayn() || (activity instanceof FragmentActivity);
        zzaa.zzb(z, (Object) "This Activity is not supported before platform version 11 (3.0 Honeycomb). Please use FragmentActivity instead.");
        this.Be = activity;
    }

    public boolean zzatv() {
        return this.Be instanceof FragmentActivity;
    }

    public Activity zzatw() {
        return (Activity) this.Be;
    }

    public FragmentActivity zzatx() {
        return (FragmentActivity) this.Be;
    }
}
