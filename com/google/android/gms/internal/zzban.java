package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class zzban extends zzbam {
    protected final TaskCompletionSource<Void> zzalE;

    public zzban(int i, TaskCompletionSource<Void> taskCompletionSource) {
        super(i);
        this.zzalE = taskCompletionSource;
    }

    public void zza(@NonNull zzbbt com_google_android_gms_internal_zzbbt, boolean z) {
    }

    public final void zza(zzbdd<?> com_google_android_gms_internal_zzbdd_) throws DeadObjectException {
        try {
            zzb(com_google_android_gms_internal_zzbdd_);
        } catch (RemoteException e) {
            zzp(zzbam.zza(e));
            throw e;
        } catch (RemoteException e2) {
            zzp(zzbam.zza(e2));
        }
    }

    protected abstract void zzb(zzbdd<?> com_google_android_gms_internal_zzbdd_) throws RemoteException;

    public void zzp(@NonNull Status status) {
        this.zzalE.trySetException(new ApiException(status));
    }
}
