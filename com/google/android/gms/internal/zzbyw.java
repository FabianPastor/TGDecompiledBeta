package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbyw implements zzbzb {
    private final zzbzb zzcxZ;

    public void close() throws IOException {
        this.zzcxZ.close();
    }

    public long read(zzbyr com_google_android_gms_internal_zzbyr, long j) throws IOException {
        return this.zzcxZ.read(com_google_android_gms_internal_zzbyr, j);
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + this.zzcxZ.toString() + ")";
    }
}
