package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzbee<A extends zzb, L> {
    private final zzbdw<L> zzaEU;

    protected zzbee(zzbdw<L> com_google_android_gms_internal_zzbdw_L) {
        this.zzaEU = com_google_android_gms_internal_zzbdw_L;
    }

    protected abstract void zzb(A a, TaskCompletionSource<Void> taskCompletionSource) throws RemoteException;

    public final zzbdy<L> zzqG() {
        return this.zzaEU.zzqG();
    }

    public final void zzqH() {
        this.zzaEU.clear();
    }
}
