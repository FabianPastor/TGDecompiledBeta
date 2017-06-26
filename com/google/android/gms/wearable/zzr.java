package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzl;

final class zzr implements Runnable {
    private /* synthetic */ zzl zzbRD;
    private /* synthetic */ zzc zzbRy;

    zzr(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzl com_google_android_gms_wearable_internal_zzl) {
        this.zzbRy = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRD = com_google_android_gms_wearable_internal_zzl;
    }

    public final void run() {
        this.zzbRy.zzbRv.onNotificationReceived(this.zzbRD);
    }
}
