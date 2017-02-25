package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzaa;

public class zzbtj {
    private String zzaiJ;

    public zzbtj(@Nullable String str) {
        this.zzaiJ = str;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzbtj)) {
            return false;
        }
        return zzaa.equal(this.zzaiJ, ((zzbtj) obj).zzaiJ);
    }

    @Nullable
    public String getToken() {
        return this.zzaiJ;
    }

    public int hashCode() {
        return zzaa.hashCode(this.zzaiJ);
    }

    public String toString() {
        return zzaa.zzv(this).zzg("token", this.zzaiJ).toString();
    }
}
