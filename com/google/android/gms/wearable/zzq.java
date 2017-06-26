package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzaa;

final class zzq implements Runnable {
    private /* synthetic */ zzaa zzbRC;
    private /* synthetic */ zzc zzbRy;

    zzq(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzaa com_google_android_gms_wearable_internal_zzaa) {
        this.zzbRy = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRC = com_google_android_gms_wearable_internal_zzaa;
    }

    public final void run() {
        this.zzbRy.zzbRv.onCapabilityChanged(this.zzbRC);
    }
}
