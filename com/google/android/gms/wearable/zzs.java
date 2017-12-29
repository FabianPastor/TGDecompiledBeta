package com.google.android.gms.wearable;

import com.google.android.gms.wearable.internal.zzi;

final class zzs implements Runnable {
    private /* synthetic */ zzd zzlho;
    private /* synthetic */ zzi zzlhu;

    zzs(zzd com_google_android_gms_wearable_WearableListenerService_zzd, zzi com_google_android_gms_wearable_internal_zzi) {
        this.zzlho = com_google_android_gms_wearable_WearableListenerService_zzd;
        this.zzlhu = com_google_android_gms_wearable_internal_zzi;
    }

    public final void run() {
        this.zzlho.zzlhk.onEntityUpdate(this.zzlhu);
    }
}
