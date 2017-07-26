package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Result;

public final class zzbdj<O extends ApiOptions> extends zzbbz {
    private final GoogleApi<O> zzaEz;

    public zzbdj(GoogleApi<O> googleApi) {
        super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
        this.zzaEz = googleApi;
    }

    public final Context getContext() {
        return this.zzaEz.getApplicationContext();
    }

    public final Looper getLooper() {
        return this.zzaEz.getLooper();
    }

    public final void zza(zzbes com_google_android_gms_internal_zzbes) {
    }

    public final void zzb(zzbes com_google_android_gms_internal_zzbes) {
    }

    public final <A extends zzb, R extends Result, T extends zzbay<R, A>> T zzd(@NonNull T t) {
        return this.zzaEz.zza((zzbay) t);
    }

    public final <A extends zzb, T extends zzbay<? extends Result, A>> T zze(@NonNull T t) {
        return this.zzaEz.zzb((zzbay) t);
    }
}
