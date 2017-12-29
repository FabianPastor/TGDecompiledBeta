package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzfe;

final class zzm implements Runnable {
    private /* synthetic */ zzd zzlho;
    private /* synthetic */ zzfe zzlhp;

    zzm(zzd com_google_android_gms_wearable_WearableListenerService_zzd, zzfe com_google_android_gms_wearable_internal_zzfe) {
        this.zzlho = com_google_android_gms_wearable_WearableListenerService_zzd;
        this.zzlhp = com_google_android_gms_wearable_internal_zzfe;
    }

    public final void run() {
        this.zzlho.zzlhk.onMessageReceived(this.zzlhp);
    }
}
