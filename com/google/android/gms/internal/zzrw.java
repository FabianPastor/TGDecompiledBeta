package com.google.android.gms.internal;

import android.os.DeadObjectException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzrw<A extends zzb> {
    private final zzrr<?> Bt;

    protected abstract void zza(A a, TaskCompletionSource<Void> taskCompletionSource) throws DeadObjectException;

    public zzrr.zzb<?> zzatz() {
        return this.Bt.zzatz();
    }

    public void zzaua() {
        this.Bt.clear();
    }
}
