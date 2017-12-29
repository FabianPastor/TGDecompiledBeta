package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;

final class zzcw implements Runnable {
    private /* synthetic */ zzcv zzfuv;

    zzcw(zzcv com_google_android_gms_common_api_internal_zzcv) {
        this.zzfuv = com_google_android_gms_common_api_internal_zzcv;
    }

    public final void run() {
        this.zzfuv.zzfuu.zzh(new ConnectionResult(4));
    }
}
