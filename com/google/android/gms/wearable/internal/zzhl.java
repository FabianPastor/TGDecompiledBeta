package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.zzcl;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataEventBuffer;

final class zzhl implements zzcl<DataListener> {
    private /* synthetic */ DataHolder zzlhn;

    zzhl(DataHolder dataHolder) {
        this.zzlhn = dataHolder;
    }

    public final void zzahz() {
        this.zzlhn.close();
    }

    public final /* synthetic */ void zzu(Object obj) {
        try {
            ((DataListener) obj).onDataChanged(new DataEventBuffer(this.zzlhn));
        } finally {
            this.zzlhn.close();
        }
    }
}
