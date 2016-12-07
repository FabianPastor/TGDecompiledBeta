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

public final class zzaab<O extends ApiOptions> extends zzc<O> {
    private final zza<? extends zzaxn, zzaxo> zzaxY;
    private final zze zzazq;
    private final zzzy zzazr;
    private final zzg zzazs;

    public zzaab(@NonNull Context context, Api<O> api, Looper looper, @NonNull zze com_google_android_gms_common_api_Api_zze, @NonNull zzzy com_google_android_gms_internal_zzzy, zzg com_google_android_gms_common_internal_zzg, zza<? extends zzaxn, zzaxo> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo) {
        super(context, api, looper);
        this.zzazq = com_google_android_gms_common_api_Api_zze;
        this.zzazr = com_google_android_gms_internal_zzzy;
        this.zzazs = com_google_android_gms_common_internal_zzg;
        this.zzaxY = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo;
        this.zzaxK.zza((zzc) this);
    }

    public zze buildApiClient(Looper looper, zzaap.zza<O> com_google_android_gms_internal_zzaap_zza_O) {
        this.zzazr.zza(com_google_android_gms_internal_zzaap_zza_O);
        return this.zzazq;
    }

    public zzabj createSignInCoordinator(Context context, Handler handler) {
        return new zzabj(context, handler, this.zzazs, this.zzaxY);
    }

    public zze zzvr() {
        return this.zzazq;
    }
}
