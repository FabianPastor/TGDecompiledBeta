package com.google.android.gms.common.api.internal;

final class zzbt implements Runnable {
    private /* synthetic */ zzbs zzftt;

    zzbt(zzbs com_google_android_gms_common_api_internal_zzbs) {
        this.zzftt = com_google_android_gms_common_api_internal_zzbs;
    }

    public final void run() {
        this.zzftt.zzftr.zzfpv.disconnect();
    }
}
