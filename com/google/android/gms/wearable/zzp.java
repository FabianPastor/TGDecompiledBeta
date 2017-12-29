package com.google.android.gms.wearable;

import java.util.List;

final class zzp implements Runnable {
    private /* synthetic */ zzd zzlho;
    private /* synthetic */ List zzlhr;

    zzp(zzd com_google_android_gms_wearable_WearableListenerService_zzd, List list) {
        this.zzlho = com_google_android_gms_wearable_WearableListenerService_zzd;
        this.zzlhr = list;
    }

    public final void run() {
        this.zzlho.zzlhk.onConnectedNodes(this.zzlhr);
    }
}
