package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzdn<A extends zzb, L> {
    protected abstract void zzc(A a, TaskCompletionSource<Boolean> taskCompletionSource) throws RemoteException;
}
