package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class zzb<T> implements Iterator<T> {
    protected final DataBuffer<T> zH;
    protected int zI = -1;

    public zzb(DataBuffer<T> dataBuffer) {
        this.zH = (DataBuffer) zzac.zzy(dataBuffer);
    }

    public boolean hasNext() {
        return this.zI < this.zH.getCount() + -1;
    }

    public T next() {
        if (hasNext()) {
            DataBuffer dataBuffer = this.zH;
            int i = this.zI + 1;
            this.zI = i;
            return dataBuffer.get(i);
        }
        throw new NoSuchElementException("Cannot advance the iterator beyond " + this.zI);
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from a DataBufferIterator");
    }
}
