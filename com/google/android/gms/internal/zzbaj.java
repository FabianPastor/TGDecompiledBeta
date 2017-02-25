package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzbaj implements Optional {
    public static final zzbaj zzbEm = new zza().zzPN();
    private final boolean zzajv;
    private final String zzajw;
    private final boolean zzakm;
    private final String zzakn;
    private final boolean zzbEn;
    private final boolean zzbEo;
    private final Long zzbEp;
    private final Long zzbEq;

    public static final class zza {
        public zzbaj zzPN() {
            return new zzbaj(false, false, null, false, null, false, null, null);
        }
    }

    private zzbaj(boolean z, boolean z2, String str, boolean z3, String str2, boolean z4, Long l, Long l2) {
        this.zzbEn = z;
        this.zzajv = z2;
        this.zzajw = str;
        this.zzakm = z3;
        this.zzbEo = z4;
        this.zzakn = str2;
        this.zzbEp = l;
        this.zzbEq = l2;
    }

    public String getServerClientId() {
        return this.zzajw;
    }

    public boolean isIdTokenRequested() {
        return this.zzajv;
    }

    public boolean zzPJ() {
        return this.zzbEn;
    }

    public boolean zzPK() {
        return this.zzbEo;
    }

    @Nullable
    public Long zzPL() {
        return this.zzbEp;
    }

    @Nullable
    public Long zzPM() {
        return this.zzbEq;
    }

    public boolean zzrl() {
        return this.zzakm;
    }

    @Nullable
    public String zzrm() {
        return this.zzakn;
    }
}
