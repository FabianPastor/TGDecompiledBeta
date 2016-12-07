package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzab;

public class zzanc {
    private String fG;

    public zzanc(@Nullable String str) {
        this.fG = str;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzanc)) {
            return false;
        }
        return zzab.equal(this.fG, ((zzanc) obj).fG);
    }

    @Nullable
    public String getToken() {
        return this.fG;
    }

    public int hashCode() {
        return zzab.hashCode(this.fG);
    }

    public String toString() {
        return zzab.zzx(this).zzg("token", this.fG).toString();
    }
}
