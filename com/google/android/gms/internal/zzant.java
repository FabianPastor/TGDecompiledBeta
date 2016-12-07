package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzz;

public class zzant {
    private String hN;

    public zzant(@Nullable String str) {
        this.hN = str;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzant)) {
            return false;
        }
        return zzz.equal(this.hN, ((zzant) obj).hN);
    }

    @Nullable
    public String getToken() {
        return this.hN;
    }

    public int hashCode() {
        return zzz.hashCode(this.hN);
    }

    public String toString() {
        return zzz.zzx(this).zzg("token", this.hN).toString();
    }
}
