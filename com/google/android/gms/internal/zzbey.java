package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzbey<A extends zzb, L> {
    private final zzbdy<L> zzaEN;

    protected zzbey(zzbdy<L> com_google_android_gms_internal_zzbdy_L) {
        this.zzaEN = com_google_android_gms_internal_zzbdy_L;
    }

    protected abstract void zzc(A a, TaskCompletionSource<Void> taskCompletionSource) throws RemoteException;

    public final zzbdy<L> zzqG() {
        return this.zzaEN;
    }
}
