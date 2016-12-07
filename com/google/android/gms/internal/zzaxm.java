package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.HasOptions;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzg;

public final class zzaxm {
    public static final Api<zzaxo> API = new Api("SignIn.API", zzahd, zzahc);
    public static final Api<zza> zzaJq = new Api("SignIn.INTERNAL_API", zzbCe, zzbCd);
    public static final zzf<zzaxy> zzahc = new zzf();
    public static final com.google.android.gms.common.api.Api.zza<zzaxy, zzaxo> zzahd = new com.google.android.gms.common.api.Api.zza<zzaxy, zzaxo>() {
        public zzaxy zza(Context context, Looper looper, zzg com_google_android_gms_common_internal_zzg, zzaxo com_google_android_gms_internal_zzaxo, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzaxy(context, looper, true, com_google_android_gms_common_internal_zzg, com_google_android_gms_internal_zzaxo == null ? zzaxo.zzbCg : com_google_android_gms_internal_zzaxo, connectionCallbacks, onConnectionFailedListener);
        }
    };
    public static final Scope zzajd = new Scope(Scopes.PROFILE);
    public static final Scope zzaje = new Scope("email");
    public static final zzf<zzaxy> zzbCd = new zzf();
    static final com.google.android.gms.common.api.Api.zza<zzaxy, zza> zzbCe = new com.google.android.gms.common.api.Api.zza<zzaxy, zza>() {
        public zzaxy zza(Context context, Looper looper, zzg com_google_android_gms_common_internal_zzg, zza com_google_android_gms_internal_zzaxm_zza, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzaxy(context, looper, false, com_google_android_gms_common_internal_zzg, com_google_android_gms_internal_zzaxm_zza.zzOd(), connectionCallbacks, onConnectionFailedListener);
        }
    };

    public static class zza implements HasOptions {
        private final Bundle zzbCf;

        public Bundle zzOd() {
            return this.zzbCf;
        }
    }
}
