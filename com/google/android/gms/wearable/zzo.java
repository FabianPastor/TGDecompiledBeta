package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzeg;

final class zzo implements Runnable {
    private /* synthetic */ zzeg zzbRA;
    private /* synthetic */ zzc zzbRy;

    zzo(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzeg com_google_android_gms_wearable_internal_zzeg) {
        this.zzbRy = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRA = com_google_android_gms_wearable_internal_zzeg;
    }

    public final void run() {
        this.zzbRy.zzbRv.onPeerDisconnected(this.zzbRA);
    }
}
