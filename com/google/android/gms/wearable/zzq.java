package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzah;

final class zzq implements Runnable {
    private /* synthetic */ zzd zzlho;
    private /* synthetic */ zzah zzlhs;

    zzq(zzd com_google_android_gms_wearable_WearableListenerService_zzd, zzah com_google_android_gms_wearable_internal_zzah) {
        this.zzlho = com_google_android_gms_wearable_WearableListenerService_zzd;
        this.zzlhs = com_google_android_gms_wearable_internal_zzah;
    }

    public final void run() {
        this.zzlho.zzlhk.onCapabilityChanged(this.zzlhs);
    }
}
