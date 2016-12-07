package com.google.android.gms.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.zzaaz.zzc;

public abstract class zzaaf<L> implements zzc<L> {
    private final DataHolder zzazI;

    protected zzaaf(DataHolder dataHolder) {
        this.zzazI = dataHolder;
    }

    protected abstract void zza(L l, DataHolder dataHolder);

    public final void zzs(L l) {
        zza(l, this.zzazI);
    }

    public void zzvy() {
        if (this.zzazI != null) {
            this.zzazI.close();
        }
    }
}
