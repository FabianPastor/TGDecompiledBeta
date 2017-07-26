package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

public final class aae {
    private String zzakv;

    public aae(@Nullable String str) {
        this.zzakv = str;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof aae)) {
            return false;
        }
        return zzbe.equal(this.zzakv, ((aae) obj).zzakv);
    }

    @Nullable
    public final String getToken() {
        return this.zzakv;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzakv});
    }

    public final String toString() {
        return zzbe.zzt(this).zzg("token", this.zzakv).toString();
    }
}
