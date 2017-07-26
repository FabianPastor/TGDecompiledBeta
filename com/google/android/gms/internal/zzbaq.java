package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbaq<TResult> extends zzbam {
    private final zzbeq<zzb, TResult> zzaBw;
    private final zzbem zzaBx;
    private final TaskCompletionSource<TResult> zzalE;

    public zzbaq(int i, zzbeq<zzb, TResult> com_google_android_gms_internal_zzbeq_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzbem com_google_android_gms_internal_zzbem) {
        super(i);
        this.zzalE = taskCompletionSource;
        this.zzaBw = com_google_android_gms_internal_zzbeq_com_google_android_gms_common_api_Api_zzb__TResult;
        this.zzaBx = com_google_android_gms_internal_zzbem;
    }

    public final void zza(@NonNull zzbbt com_google_android_gms_internal_zzbbt, boolean z) {
        com_google_android_gms_internal_zzbbt.zza(this.zzalE, z);
    }

    public final void zza(zzbdd<?> com_google_android_gms_internal_zzbdd_) throws DeadObjectException {
        try {
            this.zzaBw.zza(com_google_android_gms_internal_zzbdd_.zzpJ(), this.zzalE);
        } catch (DeadObjectException e) {
            throw e;
        } catch (RemoteException e2) {
            zzp(zzbam.zza(e2));
        }
    }

    public final void zzp(@NonNull Status status) {
        this.zzalE.trySetException(this.zzaBx.zzq(status));
    }
}
