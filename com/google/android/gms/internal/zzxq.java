package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzxq implements Optional {
    public static final zzxq aDl = new zza().zzcdh();
    private final boolean aDm;
    private final boolean aDn;
    private final Long aDo;
    private final Long aDp;
    private final boolean jr;
    private final boolean jt;
    private final String ju;
    private final String jv;

    public static final class zza {
        public zzxq zzcdh() {
            return new zzxq(false, false, null, false, null, false, null, null);
        }
    }

    private zzxq(boolean z, boolean z2, String str, boolean z3, String str2, boolean z4, Long l, Long l2) {
        this.aDm = z;
        this.jr = z2;
        this.ju = str;
        this.jt = z3;
        this.aDn = z4;
        this.jv = str2;
        this.aDo = l;
        this.aDp = l2;
    }

    public boolean zzaiu() {
        return this.jr;
    }

    public boolean zzaiw() {
        return this.jt;
    }

    public String zzaix() {
        return this.ju;
    }

    @Nullable
    public String zzaiy() {
        return this.jv;
    }

    public boolean zzcdd() {
        return this.aDm;
    }

    public boolean zzcde() {
        return this.aDn;
    }

    @Nullable
    public Long zzcdf() {
        return this.aDo;
    }

    @Nullable
    public Long zzcdg() {
        return this.aDp;
    }
}
