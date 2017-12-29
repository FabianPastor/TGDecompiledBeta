package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzcxd;
import com.google.android.gms.internal.zzcxe;

public final class zzz<O extends ApiOptions> extends GoogleApi<O> {
    private final zza<? extends zzcxd, zzcxe> zzfmz;
    private final zze zzfpv;
    private final zzt zzfpw;
    private final zzr zzfpx;

    public zzz(Context context, Api<O> api, Looper looper, zze com_google_android_gms_common_api_Api_zze, zzt com_google_android_gms_common_api_internal_zzt, zzr com_google_android_gms_common_internal_zzr, zza<? extends zzcxd, zzcxe> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzcxd__com_google_android_gms_internal_zzcxe) {
        super(context, api, looper);
        this.zzfpv = com_google_android_gms_common_api_Api_zze;
        this.zzfpw = com_google_android_gms_common_api_internal_zzt;
        this.zzfpx = com_google_android_gms_common_internal_zzr;
        this.zzfmz = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzcxd__com_google_android_gms_internal_zzcxe;
        this.zzfmi.zza((GoogleApi) this);
    }

    public final zze zza(Looper looper, zzbo<O> com_google_android_gms_common_api_internal_zzbo_O) {
        this.zzfpw.zza(com_google_android_gms_common_api_internal_zzbo_O);
        return this.zzfpv;
    }

    public final zzcv zza(Context context, Handler handler) {
        return new zzcv(context, handler, this.zzfpx, this.zzfmz);
    }

    public final zze zzahp() {
        return this.zzfpv;
    }
}
