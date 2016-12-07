package com.google.android.gms.common.data;

import java.util.NoSuchElementException;

public class zzg<T> extends zzb<T> {
    private T Ad;

    public zzg(DataBuffer<T> dataBuffer) {
        super(dataBuffer);
    }

    public T next() {
        if (hasNext()) {
            this.zI++;
            if (this.zI == 0) {
                this.Ad = this.zH.get(0);
                if (!(this.Ad instanceof zzc)) {
                    String valueOf = String.valueOf(this.Ad.getClass());
                    throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 44).append("DataBuffer reference of type ").append(valueOf).append(" is not movable").toString());
                }
            }
            ((zzc) this.Ad).zzfz(this.zI);
            return this.Ad;
        }
        throw new NoSuchElementException("Cannot advance the iterator beyond " + this.zI);
    }
}
