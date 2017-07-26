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

public final class zzbbo<O extends ApiOptions> extends GoogleApi<O> {
    private final zza<? extends zzctk, zzctl> zzaBe;
    private final zzq zzaCA;
    private final zze zzaCy;
    private final zzbbi zzaCz;

    public zzbbo(@NonNull Context context, Api<O> api, Looper looper, @NonNull zze com_google_android_gms_common_api_Api_zze, @NonNull zzbbi com_google_android_gms_internal_zzbbi, zzq com_google_android_gms_common_internal_zzq, zza<? extends zzctk, zzctl> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl) {
        super(context, api, looper);
        this.zzaCy = com_google_android_gms_common_api_Api_zze;
        this.zzaCz = com_google_android_gms_internal_zzbbi;
        this.zzaCA = com_google_android_gms_common_internal_zzq;
        this.zzaBe = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl;
        this.zzaAN.zzb((GoogleApi) this);
    }

    public final zze zza(Looper looper, zzbdd<O> com_google_android_gms_internal_zzbdd_O) {
        this.zzaCz.zza(com_google_android_gms_internal_zzbdd_O);
        return this.zzaCy;
    }

    public final zzbej zza(Context context, Handler handler) {
        return new zzbej(context, handler, this.zzaCA, this.zzaBe);
    }

    public final zze zzpJ() {
        return this.zzaCy;
    }
}
