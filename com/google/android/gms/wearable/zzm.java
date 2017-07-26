package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzdx;

final class zzm implements Runnable {
    private /* synthetic */ zzc zzbRA;
    private /* synthetic */ zzdx zzbRB;

    zzm(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzdx com_google_android_gms_wearable_internal_zzdx) {
        this.zzbRA = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRB = com_google_android_gms_wearable_internal_zzdx;
    }

    public final void run() {
        this.zzbRA.zzbRx.onMessageReceived(this.zzbRB);
    }
}
