package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;

final class zzbr implements Runnable {
    private /* synthetic */ zzbo zzftr;
    private /* synthetic */ ConnectionResult zzfts;

    zzbr(zzbo com_google_android_gms_common_api_internal_zzbo, ConnectionResult connectionResult) {
        this.zzftr = com_google_android_gms_common_api_internal_zzbo;
        this.zzfts = connectionResult;
    }

    public final void run() {
        this.zzftr.onConnectionFailed(this.zzfts);
    }
}
