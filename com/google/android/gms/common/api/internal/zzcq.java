package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzcq<A extends zzb, L> {
    private final zzci<L> zzfus;

    public final void zzajp() {
        this.zzfus.clear();
    }

    protected abstract void zzb(A a, TaskCompletionSource<Void> taskCompletionSource) throws RemoteException;
}
