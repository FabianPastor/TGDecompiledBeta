package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

public final class zzbas<O extends ApiOptions> {
    private final O zzaAJ;
    private final int zzaBA;
    private final boolean zzaBz = true;
    private final Api<O> zzayW;

    private zzbas(Api<O> api) {
        this.zzayW = api;
        this.zzaAJ = null;
        this.zzaBA = System.identityHashCode(this);
    }

    private zzbas(Api<O> api, O o) {
        this.zzayW = api;
        this.zzaAJ = o;
        this.zzaBA = Arrays.hashCode(new Object[]{this.zzayW, this.zzaAJ});
    }

    public static <O extends ApiOptions> zzbas<O> zza(Api<O> api, O o) {
        return new zzbas(api, o);
    }

    public static <O extends ApiOptions> zzbas<O> zzb(Api<O> api) {
        return new zzbas(api);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbas)) {
            return false;
        }
        zzbas com_google_android_gms_internal_zzbas = (zzbas) obj;
        return !this.zzaBz && !com_google_android_gms_internal_zzbas.zzaBz && zzbe.equal(this.zzayW, com_google_android_gms_internal_zzbas.zzayW) && zzbe.equal(this.zzaAJ, com_google_android_gms_internal_zzbas.zzaAJ);
    }

    public final int hashCode() {
        return this.zzaBA;
    }

    public final String zzpr() {
        return this.zzayW.getName();
    }
}
