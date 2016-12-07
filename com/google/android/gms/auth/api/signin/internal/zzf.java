package com.google.android.gms.auth.api.signin.internal;

public class zzf {
    static int zzajy = 31;
    private int zzajz = 1;

    public zzf zzad(boolean z) {
        this.zzajz = (z ? 1 : 0) + (this.zzajz * zzajy);
        return this;
    }

    public zzf zzq(Object obj) {
        this.zzajz = (obj == null ? 0 : obj.hashCode()) + (this.zzajz * zzajy);
        return this;
    }

    public int zzqV() {
        return this.zzajz;
    }
}
