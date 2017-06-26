package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbap<TResult> extends zzbal {
    private final zzbep<zzb, TResult> zzaBw;
    private final zzbel zzaBx;
    private final TaskCompletionSource<TResult> zzalE;

    public zzbap(int i, zzbep<zzb, TResult> com_google_android_gms_internal_zzbep_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzbel com_google_android_gms_internal_zzbel) {
        super(i);
        this.zzalE = taskCompletionSource;
        this.zzaBw = com_google_android_gms_internal_zzbep_com_google_android_gms_common_api_Api_zzb__TResult;
        this.zzaBx = com_google_android_gms_internal_zzbel;
    }

    public final void zza(@NonNull zzbbs com_google_android_gms_internal_zzbbs, boolean z) {
        com_google_android_gms_internal_zzbbs.zza(this.zzalE, z);
    }

    public final void zza(zzbdc<?> com_google_android_gms_internal_zzbdc_) throws DeadObjectException {
        try {
            this.zzaBw.zza(com_google_android_gms_internal_zzbdc_.zzpJ(), this.zzalE);
        } catch (DeadObjectException e) {
            throw e;
        } catch (RemoteException e2) {
            zzp(zzbal.zza(e2));
        }
    }

    public final void zzp(@NonNull Status status) {
        this.zzalE.trySetException(this.zzaBx.zzq(status));
    }
}
