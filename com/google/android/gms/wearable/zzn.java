package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzeg;

final class zzn implements Runnable {
    private /* synthetic */ zzc zzbRA;
    private /* synthetic */ zzeg zzbRC;

    zzn(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzeg com_google_android_gms_wearable_internal_zzeg) {
        this.zzbRA = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRC = com_google_android_gms_wearable_internal_zzeg;
    }

    public final void run() {
        this.zzbRA.zzbRx.onPeerConnected(this.zzbRC);
    }
}
