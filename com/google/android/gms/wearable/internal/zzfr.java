package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbay;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import java.util.List;
import java.util.concurrent.FutureTask;

final class zzfr extends zzfc<DataItemResult> {
    private final List<FutureTask<Boolean>> zzJQ;

    zzfr(zzbay<DataItemResult> com_google_android_gms_internal_zzbay_com_google_android_gms_wearable_DataApi_DataItemResult, List<FutureTask<Boolean>> list) {
        super(com_google_android_gms_internal_zzbay_com_google_android_gms_wearable_DataApi_DataItemResult);
        this.zzJQ = list;
    }

    public final void zza(zzem com_google_android_gms_wearable_internal_zzem) {
        zzR(new zzbs(zzev.zzaY(com_google_android_gms_wearable_internal_zzem.statusCode), com_google_android_gms_wearable_internal_zzem.zzbSN));
        if (com_google_android_gms_wearable_internal_zzem.statusCode != 0) {
            for (FutureTask cancel : this.zzJQ) {
                cancel.cancel(true);
            }
        }
    }
}