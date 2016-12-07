package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzab;

public final class zzpz<O extends ApiOptions> {
    private final Api<O> tv;
    private final O vw;
    private final boolean wo = false;
    private final int wp;

    private zzpz(Api<O> api, O o) {
        this.tv = api;
        this.vw = o;
        this.wp = zzab.hashCode(this.tv, this.vw);
    }

    public static <O extends ApiOptions> zzpz<O> zza(Api<O> api, O o) {
        return new zzpz(api, o);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzpz)) {
            return false;
        }
        zzpz com_google_android_gms_internal_zzpz = (zzpz) obj;
        return zzab.equal(this.tv, com_google_android_gms_internal_zzpz.tv) && zzab.equal(this.vw, com_google_android_gms_internal_zzpz.vw);
    }

    public int hashCode() {
        return this.wp;
    }

    public String zzaqj() {
        return this.tv.getName();
    }
}
