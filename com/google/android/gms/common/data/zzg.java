package com.google.android.gms.common.data;

import java.util.NoSuchElementException;

public class zzg<T> extends zzb<T> {
    private T zzaEe;

    public zzg(DataBuffer<T> dataBuffer) {
        super(dataBuffer);
    }

    public T next() {
        if (hasNext()) {
            this.zzaDJ++;
            if (this.zzaDJ == 0) {
                this.zzaEe = this.zzaDI.get(0);
                if (!(this.zzaEe instanceof zzc)) {
                    String valueOf = String.valueOf(this.zzaEe.getClass());
                    throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 44).append("DataBuffer reference of type ").append(valueOf).append(" is not movable").toString());
                }
            }
            ((zzc) this.zzaEe).zzcG(this.zzaDJ);
            return this.zzaEe;
        }
        throw new NoSuchElementException("Cannot advance the iterator beyond " + this.zzaDJ);
    }
}
