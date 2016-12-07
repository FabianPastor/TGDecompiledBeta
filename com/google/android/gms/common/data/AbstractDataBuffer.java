package com.google.android.gms.common.data;

import android.os.Bundle;
import java.util.Iterator;

public abstract class AbstractDataBuffer<T> implements DataBuffer<T> {
    protected final DataHolder zy;

    protected AbstractDataBuffer(DataHolder dataHolder) {
        this.zy = dataHolder;
        if (this.zy == null) {
        }
    }

    @Deprecated
    public final void close() {
        release();
    }

    public abstract T get(int i);

    public int getCount() {
        return this.zy == null ? 0 : this.zy.getCount();
    }

    @Deprecated
    public boolean isClosed() {
        return this.zy == null || this.zy.isClosed();
    }

    public Iterator<T> iterator() {
        return new zzb(this);
    }

    public void release() {
        if (this.zy != null) {
            this.zy.close();
        }
    }

    public Iterator<T> singleRefIterator() {
        return new zzg(this);
    }

    public Bundle zzaui() {
        return this.zy.zzaui();
    }
}
