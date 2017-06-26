package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzdx;

final class zzm implements Runnable {
    private /* synthetic */ zzc zzbRy;
    private /* synthetic */ zzdx zzbRz;

    zzm(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzdx com_google_android_gms_wearable_internal_zzdx) {
        this.zzbRy = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRz = com_google_android_gms_wearable_internal_zzdx;
    }

    public final void run() {
        this.zzbRy.zzbRv.onMessageReceived(this.zzbRz);
    }
}
