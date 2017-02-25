package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.common.internal.zzg;

public final class zzaaj<O extends ApiOptions> extends zzc<O> {
    private final zze zzaAJ;
    private final zzaag zzaAK;
    private final zzg zzaAL;
    private final zza<? extends zzbai, zzbaj> zzazo;

    public zzaaj(@NonNull Context context, Api<O> api, Looper looper, @NonNull zze com_google_android_gms_common_api_Api_zze, @NonNull zzaag com_google_android_gms_internal_zzaag, zzg com_google_android_gms_common_internal_zzg, zza<? extends zzbai, zzbaj> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj) {
        super(context, api, looper);
        this.zzaAJ = com_google_android_gms_common_api_Api_zze;
        this.zzaAK = com_google_android_gms_internal_zzaag;
        this.zzaAL = com_google_android_gms_common_internal_zzg;
        this.zzazo = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj;
        this.zzayX.zzb((zzc) this);
    }

    public zze buildApiClient(Looper looper, zzaax.zza<O> com_google_android_gms_internal_zzaax_zza_O) {
        this.zzaAK.zza(com_google_android_gms_internal_zzaax_zza_O);
        return this.zzaAJ;
    }

    public zzabr createSignInCoordinator(Context context, Handler handler) {
        return new zzabr(context, handler, this.zzaAL, this.zzazo);
    }

    public zze zzvU() {
        return this.zzaAJ;
    }
}
