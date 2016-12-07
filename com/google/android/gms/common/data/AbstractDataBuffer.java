package com.google.android.gms.common.data;

import android.os.Bundle;
import java.util.Iterator;

public abstract class AbstractDataBuffer<T> implements DataBuffer<T> {
    protected final DataHolder zzazI;

    protected AbstractDataBuffer(DataHolder dataHolder) {
        this.zzazI = dataHolder;
    }

    @Deprecated
    public final void close() {
        release();
    }

    public abstract T get(int i);

    public int getCount() {
        return this.zzazI == null ? 0 : this.zzazI.getCount();
    }

    @Deprecated
    public boolean isClosed() {
        return this.zzazI == null || this.zzazI.isClosed();
    }

    public Iterator<T> iterator() {
        return new zzb(this);
    }

    public void release() {
        if (this.zzazI != null) {
            this.zzazI.close();
        }
    }

    public Iterator<T> singleRefIterator() {
        return new zzg(this);
    }

    public Bundle zzwy() {
        return this.zzazI.zzwy();
    }
}
