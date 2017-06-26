package com.google.android.gms.auth.api.signin.internal;

public final class zzo {
    private static int zzams = 31;
    private int zzamt = 1;

    public final zzo zzP(boolean z) {
        this.zzamt = (z ? 1 : 0) + (this.zzamt * zzams);
        return this;
    }

    public final int zzmJ() {
        return this.zzamt;
    }

    public final zzo zzo(Object obj) {
        this.zzamt = (obj == null ? 0 : obj.hashCode()) + (this.zzamt * zzams);
        return this;
    }
}
