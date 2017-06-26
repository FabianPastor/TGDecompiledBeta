package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzbex<A extends zzb, L> {
    private final zzbdx<L> zzaEN;

    protected zzbex(zzbdx<L> com_google_android_gms_internal_zzbdx_L) {
        this.zzaEN = com_google_android_gms_internal_zzbdx_L;
    }

    protected abstract void zzc(A a, TaskCompletionSource<Void> taskCompletionSource) throws RemoteException;

    public final zzbdx<L> zzqG() {
        return this.zzaEN;
    }
}
