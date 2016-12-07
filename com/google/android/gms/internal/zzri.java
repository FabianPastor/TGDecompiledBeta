package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.internal.zzqo.zza;

public class zzri<O extends ApiOptions> extends zzqz {
    private final zzc<O> AY;

    public zzri(zzc<O> com_google_android_gms_common_api_zzc_O) {
        super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
        this.AY = com_google_android_gms_common_api_zzc_O;
    }

    public Looper getLooper() {
        return this.AY.getLooper();
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zza(@NonNull T t) {
        return this.AY.doRead((zza) t);
    }

    public void zza(zzsf com_google_android_gms_internal_zzsf) {
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzb(@NonNull T t) {
        return this.AY.doWrite((zza) t);
    }

    public void zzb(zzsf com_google_android_gms_internal_zzsf) {
    }
}
