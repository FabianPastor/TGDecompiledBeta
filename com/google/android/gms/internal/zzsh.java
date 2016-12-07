package com.google.android.gms.internal;

import android.os.DeadObjectException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzsh<A extends zzb> {
    private final zzrr.zzb<?> Bm;

    public zzrr.zzb<?> zzatz() {
        return this.Bm;
    }

    protected abstract void zzc(A a, TaskCompletionSource<Void> taskCompletionSource) throws DeadObjectException;
}
