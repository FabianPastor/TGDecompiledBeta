package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public final class zzbao<A extends zzbay<? extends Result, zzb>> extends zzbam {
    private A zzaBt;

    public zzbao(int i, A a) {
        super(i);
        this.zzaBt = a;
    }

    public final void zza(@NonNull zzbbt com_google_android_gms_internal_zzbbt, boolean z) {
        com_google_android_gms_internal_zzbbt.zza(this.zzaBt, z);
    }

    public final void zza(zzbdd<?> com_google_android_gms_internal_zzbdd_) throws DeadObjectException {
        this.zzaBt.zzb(com_google_android_gms_internal_zzbdd_.zzpJ());
    }

    public final void zzp(@NonNull Status status) {
        this.zzaBt.zzr(status);
    }
}
