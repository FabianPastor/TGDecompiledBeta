package com.google.android.gms.common.data;

import java.util.Iterator;

public abstract class AbstractDataBuffer<T> implements DataBuffer<T> {
    protected final DataHolder zzfqt;

    protected AbstractDataBuffer(DataHolder dataHolder) {
        this.zzfqt = dataHolder;
    }

    public int getCount() {
        return this.zzfqt == null ? 0 : this.zzfqt.zzfwg;
    }

    public Iterator<T> iterator() {
        return new zzb(this);
    }

    public void release() {
        if (this.zzfqt != null) {
            this.zzfqt.close();
        }
    }
}
