package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.Api.zzg;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class zzag<T extends IInterface> extends zzj<T> {
    private final zzg<T> EO;

    public zzag(Context context, Looper looper, int i, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, zzf com_google_android_gms_common_internal_zzf, zzg<T> com_google_android_gms_common_api_Api_zzg_T) {
        super(context, looper, i, com_google_android_gms_common_internal_zzf, connectionCallbacks, onConnectionFailedListener);
        this.EO = com_google_android_gms_common_api_Api_zzg_T;
    }

    public zzg<T> zzawt() {
        return this.EO;
    }

    protected void zzc(int i, T t) {
        this.EO.zza(i, t);
    }

    protected T zzh(IBinder iBinder) {
        return this.EO.zzh(iBinder);
    }

    protected String zzjx() {
        return this.EO.zzjx();
    }

    protected String zzjy() {
        return this.EO.zzjy();
    }
}
