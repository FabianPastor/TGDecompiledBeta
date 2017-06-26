package com.google.android.gms.internal;

import com.google.android.gms.common.data.DataHolder;

public abstract class zzbbw<L> implements zzbdy<L> {
    private final DataHolder zzaCX;

    protected zzbbw(DataHolder dataHolder) {
        this.zzaCX = dataHolder;
    }

    protected abstract void zza(L l, DataHolder dataHolder);

    public final void zzpT() {
        if (this.zzaCX != null) {
            this.zzaCX.close();
        }
    }

    public final void zzq(L l) {
        zza(l, this.zzaCX);
    }
}
