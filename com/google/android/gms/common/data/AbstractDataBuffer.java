package com.google.android.gms.common.data;

import android.os.Bundle;
import java.util.Iterator;

public abstract class AbstractDataBuffer<T> implements DataBuffer<T> {
    protected final DataHolder zzaCX;

    protected AbstractDataBuffer(DataHolder dataHolder) {
        this.zzaCX = dataHolder;
    }

    @Deprecated
    public final void close() {
        release();
    }

    public abstract T get(int i);

    public int getCount() {
        return this.zzaCX == null ? 0 : this.zzaCX.zzaFG;
    }

    @Deprecated
    public boolean isClosed() {
        return this.zzaCX == null || this.zzaCX.isClosed();
    }

    public Iterator<T> iterator() {
        return new zzb(this);
    }

    public void release() {
        if (this.zzaCX != null) {
            this.zzaCX.close();
        }
    }

    public Iterator<T> singleRefIterator() {
        return new zzh(this);
    }

    public final Bundle zzqN() {
        return this.zzaCX.zzqN();
    }
}
