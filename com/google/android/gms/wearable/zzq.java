package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzaa;

final class zzq implements Runnable {
    private /* synthetic */ zzc zzbRA;
    private /* synthetic */ zzaa zzbRE;

    zzq(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzaa com_google_android_gms_wearable_internal_zzaa) {
        this.zzbRA = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRE = com_google_android_gms_wearable_internal_zzaa;
    }

    public final void run() {
        this.zzbRA.zzbRx.onCapabilityChanged(this.zzbRE);
    }
}
