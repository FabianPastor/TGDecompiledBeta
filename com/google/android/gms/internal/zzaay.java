package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.internal.zzaad.zza;

public class zzaay<O extends ApiOptions> extends zzaap {
    private final zzc<O> zzaCK;

    public zzaay(zzc<O> com_google_android_gms_common_api_zzc_O) {
        super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
        this.zzaCK = com_google_android_gms_common_api_zzc_O;
    }

    public Context getContext() {
        return this.zzaCK.getApplicationContext();
    }

    public Looper getLooper() {
        return this.zzaCK.getLooper();
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zza(@NonNull T t) {
        return this.zzaCK.doRead((zza) t);
    }

    public void zza(zzabx com_google_android_gms_internal_zzabx) {
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzb(@NonNull T t) {
        return this.zzaCK.doWrite((zza) t);
    }

    public void zzb(zzabx com_google_android_gms_internal_zzabx) {
    }
}
