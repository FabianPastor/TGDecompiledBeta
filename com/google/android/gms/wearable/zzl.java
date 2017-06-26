package com.google.android.gms.wearable;

import com.google.android.gms.common.data.DataHolder;

final class zzl implements Runnable {
    private /* synthetic */ DataHolder zzbRx;
    private /* synthetic */ zzc zzbRy;

    zzl(zzc com_google_android_gms_wearable_WearableListenerService_zzc, DataHolder dataHolder) {
        this.zzbRy = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRx = dataHolder;
    }

    public final void run() {
        DataEventBuffer dataEventBuffer = new DataEventBuffer(this.zzbRx);
        try {
            this.zzbRy.zzbRv.onDataChanged(dataEventBuffer);
        } finally {
            dataEventBuffer.release();
        }
    }
}
