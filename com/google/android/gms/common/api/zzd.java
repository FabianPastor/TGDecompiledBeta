package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.api.GoogleApi.zza;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbar;
import com.google.android.gms.internal.zzbel;

public final class zzd {
    private zzbel zzaAM;
    private Looper zzrO;

    public final zzd zza(Looper looper) {
        zzbo.zzb((Object) looper, (Object) "Looper must not be null.");
        this.zzrO = looper;
        return this;
    }

    public final zzd zza(zzbel com_google_android_gms_internal_zzbel) {
        zzbo.zzb((Object) com_google_android_gms_internal_zzbel, (Object) "StatusExceptionMapper must not be null.");
        this.zzaAM = com_google_android_gms_internal_zzbel;
        return this;
    }

    public final zza zzpj() {
        if (this.zzaAM == null) {
            this.zzaAM = new zzbar();
        }
        if (this.zzrO == null) {
            if (Looper.myLooper() != null) {
                this.zzrO = Looper.myLooper();
            } else {
                this.zzrO = Looper.getMainLooper();
            }
        }
        return new zza(this.zzaAM, this.zzrO);
    }
}
