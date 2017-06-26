package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzai;

final class zzt implements Runnable {
    private /* synthetic */ zzai zzbRF;
    private /* synthetic */ zzc zzbRy;

    zzt(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzai com_google_android_gms_wearable_internal_zzai) {
        this.zzbRy = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRF = com_google_android_gms_wearable_internal_zzai;
    }

    public final void run() {
        this.zzbRF.zza(this.zzbRy.zzbRv);
    }
}
