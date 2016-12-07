package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.internal.zzzv.zza;

public class zzaaq<O extends ApiOptions> extends zzaah {
    private final zzc<O> zzaBl;

    public zzaaq(zzc<O> com_google_android_gms_common_api_zzc_O) {
        super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
        this.zzaBl = com_google_android_gms_common_api_zzc_O;
    }

    public Looper getLooper() {
        return this.zzaBl.getLooper();
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zza(@NonNull T t) {
        return this.zzaBl.doRead((zza) t);
    }

    public void zza(zzabp com_google_android_gms_internal_zzabp) {
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzb(@NonNull T t) {
        return this.zzaBl.doWrite((zza) t);
    }

    public void zzb(zzabp com_google_android_gms_internal_zzabp) {
    }
}
