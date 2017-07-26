package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

final class zzbbv implements OnCompleteListener<TResult> {
    private /* synthetic */ zzbbt zzaCU;
    private /* synthetic */ TaskCompletionSource zzaCV;

    zzbbv(zzbbt com_google_android_gms_internal_zzbbt, TaskCompletionSource taskCompletionSource) {
        this.zzaCU = com_google_android_gms_internal_zzbbt;
        this.zzaCV = taskCompletionSource;
    }

    public final void onComplete(@NonNull Task<TResult> task) {
        this.zzaCU.zzaCS.remove(this.zzaCV);
    }
}
