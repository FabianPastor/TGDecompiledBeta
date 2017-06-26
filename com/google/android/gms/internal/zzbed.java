package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzbed<A extends zzb, L> {
    private final zzbdv<L> zzaEU;

    protected zzbed(zzbdv<L> com_google_android_gms_internal_zzbdv_L) {
        this.zzaEU = com_google_android_gms_internal_zzbdv_L;
    }

    protected abstract void zzb(A a, TaskCompletionSource<Void> taskCompletionSource) throws RemoteException;

    public final zzbdx<L> zzqG() {
        return this.zzaEU.zzqG();
    }

    public final void zzqH() {
        this.zzaEU.clear();
    }
}
