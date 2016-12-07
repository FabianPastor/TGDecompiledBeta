package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzz;

public final class zzql<O extends ApiOptions> {
    private final Api<O> vS;
    private final O xw;
    private final boolean yo = true;
    private final int yp;

    private zzql(Api<O> api) {
        this.vS = api;
        this.xw = null;
        this.yp = System.identityHashCode(this);
    }

    private zzql(Api<O> api, O o) {
        this.vS = api;
        this.xw = o;
        this.yp = zzz.hashCode(this.vS, this.xw);
    }

    public static <O extends ApiOptions> zzql<O> zza(Api<O> api, O o) {
        return new zzql(api, o);
    }

    public static <O extends ApiOptions> zzql<O> zzb(Api<O> api) {
        return new zzql(api);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzql)) {
            return false;
        }
        zzql com_google_android_gms_internal_zzql = (zzql) obj;
        return !this.yo && !com_google_android_gms_internal_zzql.yo && zzz.equal(this.vS, com_google_android_gms_internal_zzql.vS) && zzz.equal(this.xw, com_google_android_gms_internal_zzql.xw);
    }

    public int hashCode() {
        return this.yp;
    }

    public String zzarl() {
        return this.vS.getName();
    }
}
