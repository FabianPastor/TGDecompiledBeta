package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzn;

final class zzdlt extends zzdlq {
    private final zzn<BooleanResult> zzgbw;

    public zzdlt(zzn<BooleanResult> com_google_android_gms_common_api_internal_zzn_com_google_android_gms_common_api_BooleanResult) {
        this.zzgbw = com_google_android_gms_common_api_internal_zzn_com_google_android_gms_common_api_BooleanResult;
    }

    public final void zza(Status status, boolean z, Bundle bundle) {
        this.zzgbw.setResult(new BooleanResult(status, z));
    }
}
