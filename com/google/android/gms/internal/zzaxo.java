package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzaxo implements Optional {
    public static final zzaxo zzbCg = new zza().zzOj();
    private final boolean zzajh;
    private final boolean zzajj;
    private final String zzajk;
    private final String zzajl;
    private final boolean zzbCh;
    private final boolean zzbCi;
    private final Long zzbCj;
    private final Long zzbCk;

    public static final class zza {
        public zzaxo zzOj() {
            return new zzaxo(false, false, null, false, null, false, null, null);
        }
    }

    private zzaxo(boolean z, boolean z2, String str, boolean z3, String str2, boolean z4, Long l, Long l2) {
        this.zzbCh = z;
        this.zzajh = z2;
        this.zzajk = str;
        this.zzajj = z3;
        this.zzbCi = z4;
        this.zzajl = str2;
        this.zzbCj = l;
        this.zzbCk = l2;
    }

    public boolean zzOf() {
        return this.zzbCh;
    }

    public boolean zzOg() {
        return this.zzbCi;
    }

    @Nullable
    public Long zzOh() {
        return this.zzbCj;
    }

    @Nullable
    public Long zzOi() {
        return this.zzbCk;
    }

    public boolean zzqK() {
        return this.zzajh;
    }

    public boolean zzqM() {
        return this.zzajj;
    }

    public String zzqN() {
        return this.zzajk;
    }

    @Nullable
    public String zzqO() {
        return this.zzajl;
    }
}
