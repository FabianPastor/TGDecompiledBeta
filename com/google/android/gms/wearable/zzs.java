package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzi;

final class zzs implements Runnable {
    private /* synthetic */ zzc zzbRA;
    private /* synthetic */ zzi zzbRG;

    zzs(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzi com_google_android_gms_wearable_internal_zzi) {
        this.zzbRA = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRG = com_google_android_gms_wearable_internal_zzi;
    }

    public final void run() {
        this.zzbRA.zzbRx.onEntityUpdate(this.zzbRG);
    }
}
