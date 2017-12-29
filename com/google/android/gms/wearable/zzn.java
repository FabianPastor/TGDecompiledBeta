package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzfo;

final class zzn implements Runnable {
    private /* synthetic */ zzd zzlho;
    private /* synthetic */ zzfo zzlhq;

    zzn(zzd com_google_android_gms_wearable_WearableListenerService_zzd, zzfo com_google_android_gms_wearable_internal_zzfo) {
        this.zzlho = com_google_android_gms_wearable_WearableListenerService_zzd;
        this.zzlhq = com_google_android_gms_wearable_internal_zzfo;
    }

    public final void run() {
        this.zzlho.zzlhk.onPeerConnected(this.zzlhq);
    }
}
