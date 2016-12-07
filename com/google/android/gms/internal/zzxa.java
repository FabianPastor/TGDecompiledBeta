package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzxa implements Optional {
    public static final zzxa aAa = new zza().zzcdf();
    private final boolean aAb;
    private final boolean aAc;
    private final Long aAd;
    private final Long aAe;
    private final boolean hh;
    private final boolean hj;
    private final String hk;
    private final String hl;

    public static final class zza {
        public zzxa zzcdf() {
            return new zzxa(false, false, null, false, null, false, null, null);
        }
    }

    private zzxa(boolean z, boolean z2, String str, boolean z3, String str2, boolean z4, Long l, Long l2) {
        this.aAb = z;
        this.hh = z2;
        this.hk = str;
        this.hj = z3;
        this.aAc = z4;
        this.hl = str2;
        this.aAd = l;
        this.aAe = l2;
    }

    public boolean zzahk() {
        return this.hh;
    }

    public boolean zzahm() {
        return this.hj;
    }

    public String zzahn() {
        return this.hk;
    }

    @Nullable
    public String zzaho() {
        return this.hl;
    }

    public boolean zzcdb() {
        return this.aAb;
    }

    public boolean zzcdc() {
        return this.aAc;
    }

    @Nullable
    public Long zzcdd() {
        return this.aAd;
    }

    @Nullable
    public Long zzcde() {
        return this.aAe;
    }
}
