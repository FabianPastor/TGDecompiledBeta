package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzaa;

public final class zzzz<O extends ApiOptions> {
    private final Api<O> zzaxf;
    private final O zzayT;
    private final boolean zzazL = true;
    private final int zzazM;

    private zzzz(Api<O> api) {
        this.zzaxf = api;
        this.zzayT = null;
        this.zzazM = System.identityHashCode(this);
    }

    private zzzz(Api<O> api, O o) {
        this.zzaxf = api;
        this.zzayT = o;
        this.zzazM = zzaa.hashCode(this.zzaxf, this.zzayT);
    }

    public static <O extends ApiOptions> zzzz<O> zza(Api<O> api, O o) {
        return new zzzz(api, o);
    }

    public static <O extends ApiOptions> zzzz<O> zzb(Api<O> api) {
        return new zzzz(api);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzzz)) {
            return false;
        }
        zzzz com_google_android_gms_internal_zzzz = (zzzz) obj;
        return !this.zzazL && !com_google_android_gms_internal_zzzz.zzazL && zzaa.equal(this.zzaxf, com_google_android_gms_internal_zzzz.zzaxf) && zzaa.equal(this.zzayT, com_google_android_gms_internal_zzzz.zzayT);
    }

    public int hashCode() {
        return this.zzazM;
    }

    public String zzvw() {
        return this.zzaxf.getName();
    }
}
