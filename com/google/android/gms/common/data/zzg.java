package com.google.android.gms.common.data;

import java.util.NoSuchElementException;

public class zzg<T> extends zzb<T> {
    private T Cn;

    public zzg(DataBuffer<T> dataBuffer) {
        super(dataBuffer);
    }

    public T next() {
        if (hasNext()) {
            this.BS++;
            if (this.BS == 0) {
                this.Cn = this.BR.get(0);
                if (!(this.Cn instanceof zzc)) {
                    String valueOf = String.valueOf(this.Cn.getClass());
                    throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 44).append("DataBuffer reference of type ").append(valueOf).append(" is not movable").toString());
                }
            }
            ((zzc) this.Cn).zzfy(this.BS);
            return this.Cn;
        }
        throw new NoSuchElementException("Cannot advance the iterator beyond " + this.BS);
    }
}
