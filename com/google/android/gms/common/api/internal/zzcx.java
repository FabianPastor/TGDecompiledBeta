package com.google.android.gms.common.api.internal;

import com.google.android.gms.internal.zzcxq;

final class zzcx implements Runnable {
    private /* synthetic */ zzcxq zzfrt;
    private /* synthetic */ zzcv zzfuv;

    zzcx(zzcv com_google_android_gms_common_api_internal_zzcv, zzcxq com_google_android_gms_internal_zzcxq) {
        this.zzfuv = com_google_android_gms_common_api_internal_zzcv;
        this.zzfrt = com_google_android_gms_internal_zzcxq;
    }

    public final void run() {
        this.zzfuv.zzc(this.zzfrt);
    }
}
