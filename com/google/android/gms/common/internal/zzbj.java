package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbj {
    private static final zzbp zzgbf = new zzbk();

    public static <R extends Result, T> Task<T> zza(PendingResult<R> pendingResult, zzbo<R, T> com_google_android_gms_common_internal_zzbo_R__T) {
        zzbp com_google_android_gms_common_internal_zzbp = zzgbf;
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        pendingResult.zza(new zzbl(pendingResult, taskCompletionSource, com_google_android_gms_common_internal_zzbo_R__T, com_google_android_gms_common_internal_zzbp));
        return taskCompletionSource.getTask();
    }
}
