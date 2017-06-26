package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class zzbam extends zzbal {
    protected final TaskCompletionSource<Void> zzalE;

    public zzbam(int i, TaskCompletionSource<Void> taskCompletionSource) {
        super(i);
        this.zzalE = taskCompletionSource;
    }

    public void zza(@NonNull zzbbs com_google_android_gms_internal_zzbbs, boolean z) {
    }

    public final void zza(zzbdc<?> com_google_android_gms_internal_zzbdc_) throws DeadObjectException {
        try {
            zzb(com_google_android_gms_internal_zzbdc_);
        } catch (RemoteException e) {
            zzp(zzbal.zza(e));
            throw e;
        } catch (RemoteException e2) {
            zzp(zzbal.zza(e2));
        }
    }

    protected abstract void zzb(zzbdc<?> com_google_android_gms_internal_zzbdc_) throws RemoteException;

    public void zzp(@NonNull Status status) {
        this.zzalE.trySetException(new ApiException(status));
    }
}
