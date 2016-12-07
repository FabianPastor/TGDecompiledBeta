package com.google.android.gms.internal;

import android.os.DeadObjectException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzrr<A extends zzb> {
    public zzrd.zzb<?> zzasr() {
        return null;
    }

    protected abstract void zzc(A a, TaskCompletionSource<Void> taskCompletionSource) throws DeadObjectException;
}
