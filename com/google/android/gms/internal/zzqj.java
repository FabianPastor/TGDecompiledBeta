package com.google.android.gms.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzrd.zzc;

public abstract class zzqj<L> implements zzc<L> {
    private final DataHolder xi;

    protected zzqj(DataHolder dataHolder) {
        this.xi = dataHolder;
    }

    protected abstract void zza(L l, DataHolder dataHolder);

    public void zzarg() {
        if (this.xi != null) {
            this.xi.close();
        }
    }

    public final void zzt(L l) {
        zza(l, this.xi);
    }
}
