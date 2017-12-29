package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Status;

final class zzaf implements zza {
    private /* synthetic */ BasePendingResult zzfqq;
    private /* synthetic */ zzae zzfqr;

    zzaf(zzae com_google_android_gms_common_api_internal_zzae, BasePendingResult basePendingResult) {
        this.zzfqr = com_google_android_gms_common_api_internal_zzae;
        this.zzfqq = basePendingResult;
    }

    public final void zzr(Status status) {
        this.zzfqr.zzfqo.remove(this.zzfqq);
    }
}
