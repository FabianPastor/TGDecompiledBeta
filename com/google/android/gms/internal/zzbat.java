package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

public final class zzbat<O extends ApiOptions> {
    private final O zzaAJ;
    private final int zzaBA;
    private final boolean zzaBz = true;
    private final Api<O> zzayW;

    private zzbat(Api<O> api) {
        this.zzayW = api;
        this.zzaAJ = null;
        this.zzaBA = System.identityHashCode(this);
    }

    private zzbat(Api<O> api, O o) {
        this.zzayW = api;
        this.zzaAJ = o;
        this.zzaBA = Arrays.hashCode(new Object[]{this.zzayW, this.zzaAJ});
    }

    public static <O extends ApiOptions> zzbat<O> zza(Api<O> api, O o) {
        return new zzbat(api, o);
    }

    public static <O extends ApiOptions> zzbat<O> zzb(Api<O> api) {
        return new zzbat(api);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbat)) {
            return false;
        }
        zzbat com_google_android_gms_internal_zzbat = (zzbat) obj;
        return !this.zzaBz && !com_google_android_gms_internal_zzbat.zzaBz && zzbe.equal(this.zzayW, com_google_android_gms_internal_zzbat.zzayW) && zzbe.equal(this.zzaAJ, com_google_android_gms_internal_zzbat.zzaAJ);
    }

    public final int hashCode() {
        return this.zzaBA;
    }

    public final String zzpr() {
        return this.zzayW.getName();
    }
}
