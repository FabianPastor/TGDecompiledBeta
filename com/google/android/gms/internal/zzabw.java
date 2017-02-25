package com.google.android.gms.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.TaskCompletionSource;

public class zzabw {
    public static void zza(Status status, TaskCompletionSource<Void> taskCompletionSource) {
        zza(status, null, taskCompletionSource);
    }

    public static <TResult> void zza(Status status, TResult tResult, TaskCompletionSource<TResult> taskCompletionSource) {
        if (status.isSuccess()) {
            taskCompletionSource.setResult(tResult);
        } else {
            taskCompletionSource.setException(new zza(status));
        }
    }
}
