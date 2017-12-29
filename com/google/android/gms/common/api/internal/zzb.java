package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class zzb<T> extends zza {
    protected final TaskCompletionSource<T> zzedx;

    public zzb(int i, TaskCompletionSource<T> taskCompletionSource) {
        super(i);
        this.zzedx = taskCompletionSource;
    }

    public void zza(zzae com_google_android_gms_common_api_internal_zzae, boolean z) {
    }

    public final void zza(zzbo<?> com_google_android_gms_common_api_internal_zzbo_) throws DeadObjectException {
        try {
            zzb(com_google_android_gms_common_api_internal_zzbo_);
        } catch (RemoteException e) {
            zzs(zza.zza(e));
            throw e;
        } catch (RemoteException e2) {
            zzs(zza.zza(e2));
        }
    }

    protected abstract void zzb(zzbo<?> com_google_android_gms_common_api_internal_zzbo_) throws RemoteException;

    public void zzs(Status status) {
        this.zzedx.trySetException(new ApiException(status));
    }
}
