package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.zzd;
import com.google.android.gms.internal.zzqc.zza;

public class zzqu<O extends ApiOptions> extends zzql {
    private final zzd<O> yN;

    public zzqu(zzd<O> com_google_android_gms_common_api_zzd_O) {
        super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
        this.yN = com_google_android_gms_common_api_zzd_O;
    }

    public Looper getLooper() {
        return this.yN.getLooper();
    }

    public void zza(zzrp com_google_android_gms_internal_zzrp) {
        this.yN.zzapu();
    }

    public void zzb(zzrp com_google_android_gms_internal_zzrp) {
        this.yN.zzapv();
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zzc(@NonNull T t) {
        return this.yN.zza((zza) t);
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzd(@NonNull T t) {
        return this.yN.zzb((zza) t);
    }
}
