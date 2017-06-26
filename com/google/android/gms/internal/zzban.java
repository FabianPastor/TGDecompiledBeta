package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public final class zzban<A extends zzbax<? extends Result, zzb>> extends zzbal {
    private A zzaBt;

    public zzban(int i, A a) {
        super(i);
        this.zzaBt = a;
    }

    public final void zza(@NonNull zzbbs com_google_android_gms_internal_zzbbs, boolean z) {
        com_google_android_gms_internal_zzbbs.zza(this.zzaBt, z);
    }

    public final void zza(zzbdc<?> com_google_android_gms_internal_zzbdc_) throws DeadObjectException {
        this.zzaBt.zzb(com_google_android_gms_internal_zzbdc_.zzpJ());
    }

    public final void zzp(@NonNull Status status) {
        this.zzaBt.zzr(status);
    }
}
