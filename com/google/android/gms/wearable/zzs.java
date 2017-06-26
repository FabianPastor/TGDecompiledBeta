package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzi;

final class zzs implements Runnable {
    private /* synthetic */ zzi zzbRE;
    private /* synthetic */ zzc zzbRy;

    zzs(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzi com_google_android_gms_wearable_internal_zzi) {
        this.zzbRy = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRE = com_google_android_gms_wearable_internal_zzi;
    }

    public final void run() {
        this.zzbRy.zzbRv.onEntityUpdate(this.zzbRE);
    }
}
