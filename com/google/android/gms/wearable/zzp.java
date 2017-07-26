package com.google.android.gms.wearable;

import java.util.List;

final class zzp implements Runnable {
    private /* synthetic */ zzc zzbRA;
    private /* synthetic */ List zzbRD;

    zzp(zzc com_google_android_gms_wearable_WearableListenerService_zzc, List list) {
        this.zzbRA = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRD = list;
    }

    public final void run() {
        this.zzbRA.zzbRx.onConnectedNodes(this.zzbRD);
    }
}
