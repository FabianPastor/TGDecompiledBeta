package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzl;

final class zzr implements Runnable {
    private /* synthetic */ zzd zzlho;
    private /* synthetic */ zzl zzlht;

    zzr(zzd com_google_android_gms_wearable_WearableListenerService_zzd, zzl com_google_android_gms_wearable_internal_zzl) {
        this.zzlho = com_google_android_gms_wearable_WearableListenerService_zzd;
        this.zzlht = com_google_android_gms_wearable_internal_zzl;
    }

    public final void run() {
        this.zzlho.zzlhk.onNotificationReceived(this.zzlht);
    }
}
