package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzaw;

final class zzt implements Runnable {
    private /* synthetic */ zzd zzlho;
    private /* synthetic */ zzaw zzlhv;

    zzt(zzd com_google_android_gms_wearable_WearableListenerService_zzd, zzaw com_google_android_gms_wearable_internal_zzaw) {
        this.zzlho = com_google_android_gms_wearable_WearableListenerService_zzd;
        this.zzlhv = com_google_android_gms_wearable_internal_zzaw;
    }

    public final void run() {
        this.zzlhv.zza(this.zzlho.zzlhk);
        this.zzlhv.zza(this.zzlho.zzlhk.zzlhj);
    }
}
