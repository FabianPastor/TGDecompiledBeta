package com.google.android.gms.dynamic;

import android.app.Activity;
import android.os.Bundle;

final class zzc implements zzi {
    private /* synthetic */ Activity val$activity;
    private /* synthetic */ zza zzaSv;
    private /* synthetic */ Bundle zzaSw;
    private /* synthetic */ Bundle zzxV;

    zzc(zza com_google_android_gms_dynamic_zza, Activity activity, Bundle bundle, Bundle bundle2) {
        this.zzaSv = com_google_android_gms_dynamic_zza;
        this.val$activity = activity;
        this.zzaSw = bundle;
        this.zzxV = bundle2;
    }

    public final int getState() {
        return 0;
    }

    public final void zzb(LifecycleDelegate lifecycleDelegate) {
        this.zzaSv.zzaSr.onInflate(this.val$activity, this.zzaSw, this.zzxV);
    }
}
