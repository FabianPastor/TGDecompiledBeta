package com.google.android.gms.dynamic;

import android.os.Bundle;

final class zzd implements zzi {
    private /* synthetic */ zza zzaSv;
    private /* synthetic */ Bundle zzxV;

    zzd(zza com_google_android_gms_dynamic_zza, Bundle bundle) {
        this.zzaSv = com_google_android_gms_dynamic_zza;
        this.zzxV = bundle;
    }

    public final int getState() {
        return 1;
    }

    public final void zzb(LifecycleDelegate lifecycleDelegate) {
        this.zzaSv.zzaSr.onCreate(this.zzxV);
    }
}
