package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzbdz;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataEventBuffer;

final class zzgb implements zzbdz<DataListener> {
    private /* synthetic */ DataHolder zzbRz;

    zzgb(DataHolder dataHolder) {
        this.zzbRz = dataHolder;
    }

    public final void zzpT() {
        this.zzbRz.close();
    }

    public final /* synthetic */ void zzq(Object obj) {
        try {
            ((DataListener) obj).onDataChanged(new DataEventBuffer(this.zzbRz));
        } finally {
            this.zzbRz.close();
        }
    }
}
