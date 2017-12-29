package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzbg;

public final class zzh<O extends ApiOptions> {
    private final Api<O> zzfin;
    private final O zzfme;
    private final boolean zzfnv = true;
    private final int zzfnw;

    private zzh(Api<O> api) {
        this.zzfin = api;
        this.zzfme = null;
        this.zzfnw = System.identityHashCode(this);
    }

    public static <O extends ApiOptions> zzh<O> zzb(Api<O> api) {
        return new zzh(api);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzh)) {
            return false;
        }
        zzh com_google_android_gms_common_api_internal_zzh = (zzh) obj;
        return !this.zzfnv && !com_google_android_gms_common_api_internal_zzh.zzfnv && zzbg.equal(this.zzfin, com_google_android_gms_common_api_internal_zzh.zzfin) && zzbg.equal(this.zzfme, com_google_android_gms_common_api_internal_zzh.zzfme);
    }

    public final int hashCode() {
        return this.zzfnw;
    }

    public final String zzagy() {
        return this.zzfin.getName();
    }
}
