package com.google.android.gms.wearable;

import java.util.List;

final class zzp implements Runnable {
    private /* synthetic */ List zzbRB;
    private /* synthetic */ zzc zzbRy;

    zzp(zzc com_google_android_gms_wearable_WearableListenerService_zzc, List list) {
        this.zzbRy = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRB = list;
    }

    public final void run() {
        this.zzbRy.zzbRv.onConnectedNodes(this.zzbRB);
    }
}
