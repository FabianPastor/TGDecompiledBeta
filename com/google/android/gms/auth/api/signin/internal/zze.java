package com.google.android.gms.auth.api.signin.internal;

public class zze {
    static int jI = 31;
    private int jJ = 1;

    public int zzajf() {
        return this.jJ;
    }

    public zze zzbe(boolean z) {
        this.jJ = (z ? 1 : 0) + (this.jJ * jI);
        return this;
    }

    public zze zzq(Object obj) {
        this.jJ = (obj == null ? 0 : obj.hashCode()) + (this.jJ * jI);
        return this;
    }
}
