package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzfjs {
    protected volatile int zzpfd = -1;

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return zzdag();
    }

    public String toString() {
        return zzfjt.zzd(this);
    }

    public abstract zzfjs zza(zzfjj com_google_android_gms_internal_zzfjj) throws IOException;

    public void zza(zzfjk com_google_android_gms_internal_zzfjk) throws IOException {
    }

    public zzfjs zzdag() throws CloneNotSupportedException {
        return (zzfjs) super.clone();
    }

    public final int zzdam() {
        if (this.zzpfd < 0) {
            zzho();
        }
        return this.zzpfd;
    }

    public final int zzho() {
        int zzq = zzq();
        this.zzpfd = zzq;
        return zzq;
    }

    protected int zzq() {
        return 0;
    }
}
