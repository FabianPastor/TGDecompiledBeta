package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class zzb<T> implements Iterator<T> {
    protected final DataBuffer<T> zzaCj;
    protected int zzaCk = -1;

    public zzb(DataBuffer<T> dataBuffer) {
        this.zzaCj = (DataBuffer) zzac.zzw(dataBuffer);
    }

    public boolean hasNext() {
        return this.zzaCk < this.zzaCj.getCount() + -1;
    }

    public T next() {
        if (hasNext()) {
            DataBuffer dataBuffer = this.zzaCj;
            int i = this.zzaCk + 1;
            this.zzaCk = i;
            return dataBuffer.get(i);
        }
        throw new NoSuchElementException("Cannot advance the iterator beyond " + this.zzaCk);
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from a DataBufferIterator");
    }
}
