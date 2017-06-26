package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.Status;

final class gw extends gv {
    private final zzbay<BooleanResult> zzaIz;

    public gw(zzbay<BooleanResult> com_google_android_gms_internal_zzbay_com_google_android_gms_common_api_BooleanResult) {
        super();
        this.zzaIz = com_google_android_gms_internal_zzbay_com_google_android_gms_common_api_BooleanResult;
    }

    public final void zza(Status status, boolean z, Bundle bundle) {
        this.zzaIz.setResult(new BooleanResult(status, z));
    }
}
