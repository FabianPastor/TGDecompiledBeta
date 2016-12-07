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
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.signin.internal.zzg;

public final class zzwy {
    public static final Api<zzxa> API = new Api("SignIn.API", fb, fa);
    public static final Api<zza> Hp = new Api("SignIn.INTERNAL_API", azZ, azY);
    public static final zzf<zzg> azY = new zzf();
    static final com.google.android.gms.common.api.Api.zza<zzg, zza> azZ = new com.google.android.gms.common.api.Api.zza<zzg, zza>() {
        public zzg zza(Context context, Looper looper, zzh com_google_android_gms_common_internal_zzh, zza com_google_android_gms_internal_zzwy_zza, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzg(context, looper, false, com_google_android_gms_common_internal_zzh, com_google_android_gms_internal_zzwy_zza.zzccz(), connectionCallbacks, onConnectionFailedListener);
        }
    };
    public static final zzf<zzg> fa = new zzf();
    public static final com.google.android.gms.common.api.Api.zza<zzg, zzxa> fb = new com.google.android.gms.common.api.Api.zza<zzg, zzxa>() {
        public zzg zza(Context context, Looper looper, zzh com_google_android_gms_common_internal_zzh, zzxa com_google_android_gms_internal_zzxa, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzg(context, looper, true, com_google_android_gms_common_internal_zzh, com_google_android_gms_internal_zzxa == null ? zzxa.aAa : com_google_android_gms_internal_zzxa, connectionCallbacks, onConnectionFailedListener);
        }
    };
    public static final Scope hd = new Scope(Scopes.PROFILE);
    public static final Scope he = new Scope("email");

    public static class zza implements HasOptions {
        public Bundle zzccz() {
            return null;
        }
    }
}
