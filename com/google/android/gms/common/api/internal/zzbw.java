package com.google.android.gms.common.api.internal;

import android.os.Looper;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Result;

public final class zzbw<O extends ApiOptions> extends zzak {
    private final GoogleApi<O> zzftw;

    public zzbw(GoogleApi<O> googleApi) {
        super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
        this.zzftw = googleApi;
    }

    public final Looper getLooper() {
        return this.zzftw.getLooper();
    }

    public final void zza(zzdg com_google_android_gms_common_api_internal_zzdg) {
    }

    public final void zzb(zzdg com_google_android_gms_common_api_internal_zzdg) {
    }

    public final <A extends zzb, R extends Result, T extends zzm<R, A>> T zzd(T t) {
        return this.zzftw.zza(t);
    }

    public final <A extends zzb, T extends zzm<? extends Result, A>> T zze(T t) {
        return this.zzftw.zzb(t);
    }
}
