package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.api.GoogleApi.zza;
import com.google.android.gms.common.api.internal.zzcz;
import com.google.android.gms.common.api.internal.zzg;

public final class zzd {
    private Looper zzall;
    private zzcz zzfmh;

    public final zza zzagq() {
        if (this.zzfmh == null) {
            this.zzfmh = new zzg();
        }
        if (this.zzall == null) {
            this.zzall = Looper.getMainLooper();
        }
        return new zza(this.zzfmh, this.zzall);
    }
}
