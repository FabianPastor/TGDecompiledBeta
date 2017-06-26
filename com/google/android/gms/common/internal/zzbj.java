package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.TimeUnit;

final class zzbj implements zza {
    private /* synthetic */ PendingResult zzaIj;
    private /* synthetic */ TaskCompletionSource zzaIk;
    private /* synthetic */ zzbm zzaIl;
    private /* synthetic */ zzbn zzaIm;

    zzbj(PendingResult pendingResult, TaskCompletionSource taskCompletionSource, zzbm com_google_android_gms_common_internal_zzbm, zzbn com_google_android_gms_common_internal_zzbn) {
        this.zzaIj = pendingResult;
        this.zzaIk = taskCompletionSource;
        this.zzaIl = com_google_android_gms_common_internal_zzbm;
        this.zzaIm = com_google_android_gms_common_internal_zzbn;
    }

    public final void zzo(Status status) {
        if (status.isSuccess()) {
            this.zzaIk.setResult(this.zzaIl.zzd(this.zzaIj.await(0, TimeUnit.MILLISECONDS)));
            return;
        }
        this.zzaIk.setException(this.zzaIm.zzy(status));
    }
}
