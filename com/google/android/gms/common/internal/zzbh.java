package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbh {
    private static final zzbn zzaIi = new zzbi();

    public static <R extends Result, T extends Response<R>> Task<T> zza(PendingResult<R> pendingResult, T t) {
        return zza((PendingResult) pendingResult, new zzbk(t));
    }

    private static <R extends Result, T> Task<T> zza(PendingResult<R> pendingResult, zzbm<R, T> com_google_android_gms_common_internal_zzbm_R__T) {
        zzbn com_google_android_gms_common_internal_zzbn = zzaIi;
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        pendingResult.zza(new zzbj(pendingResult, taskCompletionSource, com_google_android_gms_common_internal_zzbm_R__T, com_google_android_gms_common_internal_zzbn));
        return taskCompletionSource.getTask();
    }

    public static <R extends Result> Task<Void> zzb(PendingResult<R> pendingResult) {
        return zza((PendingResult) pendingResult, new zzbl());
    }
}
