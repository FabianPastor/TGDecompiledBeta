package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzse<A extends zzb, TResult> {
    protected abstract void zzb(A a, TaskCompletionSource<TResult> taskCompletionSource) throws RemoteException;
}
