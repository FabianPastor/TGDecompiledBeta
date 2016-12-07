package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.zzaa;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class zzb<T> implements Iterator<T> {
    protected final DataBuffer<T> BR;
    protected int BS = -1;

    public zzb(DataBuffer<T> dataBuffer) {
        this.BR = (DataBuffer) zzaa.zzy(dataBuffer);
    }

    public boolean hasNext() {
        return this.BS < this.BR.getCount() + -1;
    }

    public T next() {
        if (hasNext()) {
            DataBuffer dataBuffer = this.BR;
            int i = this.BS + 1;
            this.BS = i;
            return dataBuffer.get(i);
        }
        throw new NoSuchElementException("Cannot advance the iterator beyond " + this.BS);
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from a DataBufferIterator");
    }
}
