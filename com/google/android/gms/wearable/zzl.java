package com.google.android.gms.wearable;

import com.google.android.gms.common.data.DataHolder;

final class zzl implements Runnable {
    private /* synthetic */ zzc zzbRA;
    private /* synthetic */ DataHolder zzbRz;

    zzl(zzc com_google_android_gms_wearable_WearableListenerService_zzc, DataHolder dataHolder) {
        this.zzbRA = com_google_android_gms_wearable_WearableListenerService_zzc;
        this.zzbRz = dataHolder;
    }

    public final void run() {
        DataEventBuffer dataEventBuffer = new DataEventBuffer(this.zzbRz);
        try {
            this.zzbRA.zzbRx.onDataChanged(dataEventBuffer);
        } finally {
            dataEventBuffer.release();
        }
    }
}
