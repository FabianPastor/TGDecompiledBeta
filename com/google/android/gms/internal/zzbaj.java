package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzbaj implements Optional {
    public static final zzbaj zzbEi = new zza().zzPO();
    private final boolean zzajv;
    private final String zzajw;
    private final boolean zzakm;
    private final String zzakn;
    private final boolean zzbEj;
    private final boolean zzbEk;
    private final Long zzbEl;
    private final Long zzbEm;

    public static final class zza {
        public zzbaj zzPO() {
            return new zzbaj(false, false, null, false, null, false, null, null);
        }
    }

    private zzbaj(boolean z, boolean z2, String str, boolean z3, String str2, boolean z4, Long l, Long l2) {
        this.zzbEj = z;
        this.zzajv = z2;
        this.zzajw = str;
        this.zzakm = z3;
        this.zzbEk = z4;
        this.zzakn = str2;
        this.zzbEl = l;
        this.zzbEm = l2;
    }

    public String getServerClientId() {
        return this.zzajw;
    }

    public boolean isIdTokenRequested() {
        return this.zzajv;
    }

    public boolean zzPK() {
        return this.zzbEj;
    }

    public boolean zzPL() {
        return this.zzbEk;
    }

    @Nullable
    public Long zzPM() {
        return this.zzbEl;
    }

    @Nullable
    public Long zzPN() {
        return this.zzbEm;
    }

    public boolean zzrl() {
        return this.zzakm;
    }

    @Nullable
    public String zzrm() {
        return this.zzakn;
    }
}
