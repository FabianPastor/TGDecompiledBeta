package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzbdy;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataEventBuffer;

final class zzgb implements zzbdy<DataListener> {
    private /* synthetic */ DataHolder zzbRx;

    zzgb(DataHolder dataHolder) {
        this.zzbRx = dataHolder;
    }

    public final void zzpT() {
        this.zzbRx.close();
    }

    public final /* synthetic */ void zzq(Object obj) {
        try {
            ((DataListener) obj).onDataChanged(new DataEventBuffer(this.zzbRx));
        } finally {
            this.zzbRx.close();
        }
    }
}
