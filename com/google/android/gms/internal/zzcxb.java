package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzr;

final class zzcxb extends zza<zzcxn, zzcxe> {
    zzcxb() {
    }

    public final /* synthetic */ zze zza(Context context, Looper looper, zzr com_google_android_gms_common_internal_zzr, Object obj, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        zzcxe com_google_android_gms_internal_zzcxe = (zzcxe) obj;
        return new zzcxn(context, looper, true, com_google_android_gms_common_internal_zzr, com_google_android_gms_internal_zzcxe == null ? zzcxe.zzkbs : com_google_android_gms_internal_zzcxe, connectionCallbacks, onConnectionFailedListener);
    }
}
