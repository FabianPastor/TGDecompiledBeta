package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.internal.zzq;

public final class zzbbn<O extends ApiOptions> extends GoogleApi<O> {
    private final zza<? extends zzctj, zzctk> zzaBe;
    private final zzq zzaCA;
    private final zze zzaCy;
    private final zzbbh zzaCz;

    public zzbbn(@NonNull Context context, Api<O> api, Looper looper, @NonNull zze com_google_android_gms_common_api_Api_zze, @NonNull zzbbh com_google_android_gms_internal_zzbbh, zzq com_google_android_gms_common_internal_zzq, zza<? extends zzctj, zzctk> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctj__com_google_android_gms_internal_zzctk) {
        super(context, api, looper);
        this.zzaCy = com_google_android_gms_common_api_Api_zze;
        this.zzaCz = com_google_android_gms_internal_zzbbh;
        this.zzaCA = com_google_android_gms_common_internal_zzq;
        this.zzaBe = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctj__com_google_android_gms_internal_zzctk;
        this.zzaAN.zzb((GoogleApi) this);
    }

    public final zze zza(Looper looper, zzbdc<O> com_google_android_gms_internal_zzbdc_O) {
        this.zzaCz.zza(com_google_android_gms_internal_zzbdc_O);
        return this.zzaCy;
    }

    public final zzbei zza(Context context, Handler handler) {
        return new zzbei(context, handler, this.zzaCA, this.zzaBe);
    }

    public final zze zzpJ() {
        return this.zzaCy;
    }
}
