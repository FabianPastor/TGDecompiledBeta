package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzai;

final class zzt implements Runnable {
    private /* synthetic */ zzc zzbRA;
    private /* synthetic */ zzai zzbRH;

    zzt(zzc com_google_android_gms_wearable_WearableListenerService_zzc, zzai com_google_android_gms_wearable_internal_zzai) {
        this.zzbRA = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRH = com_google_android_gms_wearable_internal_zzai;
    }

    public final void run() {
        this.zzbRH.zza(this.zzbRA.zzbRx);
    }
}
