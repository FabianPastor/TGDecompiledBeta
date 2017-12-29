package com.google.android.gms.wearable;

import com.google.android.gms.common.data.AbstractDataBuffer;
import com.google.android.gms.common.data.DataHolder;

final class zzl implements Runnable {
    private /* synthetic */ DataHolder zzlhn;
    private /* synthetic */ zzd zzlho;

    zzl(zzd com_google_android_gms_wearable_WearableListenerService_zzd, DataHolder dataHolder) {
        this.zzlho = com_google_android_gms_wearable_WearableListenerService_zzd;
        this.zzlhn = dataHolder;
    }

    public final void run() {
        AbstractDataBuffer dataEventBuffer = new DataEventBuffer(this.zzlhn);
        try {
            this.zzlho.zzlhk.onDataChanged(dataEventBuffer);
        } finally {
            dataEventBuffer.release();
        }
    }
}
