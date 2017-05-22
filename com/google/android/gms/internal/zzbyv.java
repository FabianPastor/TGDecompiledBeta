package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbyv implements zzbza {
    private final zzbza zzcxY;

    public void close() throws IOException {
        this.zzcxY.close();
    }

    public void flush() throws IOException {
        this.zzcxY.flush();
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + this.zzcxY.toString() + ")";
    }

    public void write(zzbyr com_google_android_gms_internal_zzbyr, long j) throws IOException {
        this.zzcxY.write(com_google_android_gms_internal_zzbyr, j);
    }
}
