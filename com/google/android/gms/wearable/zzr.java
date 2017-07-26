package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzl;

final class zzr implements Runnable {
    private /* synthetic */ zzc zzbRA;
    private /* synthetic */ zzl zzbRF;

    zzr(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzl com_google_android_gms_wearable_internal_zzl) {
        this.zzbRA = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRF = com_google_android_gms_wearable_internal_zzl;
    }

    public final void run() {
        this.zzbRA.zzbRx.onNotificationReceived(this.zzbRF);
    }
}
