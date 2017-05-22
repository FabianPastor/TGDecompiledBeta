package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzbaj implements Optional {
    public static final zzbaj zzbEl = new zza().zzPQ();
    private final boolean zzajv;
    private final String zzajw;
    private final boolean zzakm;
    private final String zzakn;
    private final boolean zzbEm;
    private final boolean zzbEn;
    private final Long zzbEo;
    private final Long zzbEp;

    public static final class zza {
        public zzbaj zzPQ() {
            return new zzbaj(false, false, null, false, null, false, null, null);
        }
    }

    private zzbaj(boolean z, boolean z2, String str, boolean z3, String str2, boolean z4, Long l, Long l2) {
        this.zzbEm = z;
        this.zzajv = z2;
        this.zzajw = str;
        this.zzakm = z3;
        this.zzbEn = z4;
        this.zzakn = str2;
        this.zzbEo = l;
        this.zzbEp = l2;
    }

    public String getServerClientId() {
        return this.zzajw;
    }

    public boolean isIdTokenRequested() {
        return this.zzajv;
    }

    public boolean zzPM() {
        return this.zzbEm;
    }

    public boolean zzPN() {
        return this.zzbEn;
    }

    @Nullable
    public Long zzPO() {
        return this.zzbEo;
    }

    @Nullable
    public Long zzPP() {
        return this.zzbEp;
    }

    public boolean zzrl() {
        return this.zzakm;
    }

    @Nullable
    public String zzrm() {
        return this.zzakn;
    }
}
