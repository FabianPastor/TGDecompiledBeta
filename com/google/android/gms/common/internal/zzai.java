package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.Api.zzg;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class zzai<T extends IInterface> extends zzl<T> {
    private final zzg<T> Db;

    public zzai(Context context, Looper looper, int i, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, zzh com_google_android_gms_common_internal_zzh, zzg<T> com_google_android_gms_common_api_Api_zzg_T) {
        super(context, looper, i, com_google_android_gms_common_internal_zzh, connectionCallbacks, onConnectionFailedListener);
        this.Db = com_google_android_gms_common_api_Api_zzg_T;
    }

    public zzg<T> zzavk() {
        return this.Db;
    }

    protected void zzc(int i, T t) {
        this.Db.zza(i, t);
    }

    protected T zzh(IBinder iBinder) {
        return this.Db.zzh(iBinder);
    }

    protected String zzix() {
        return this.Db.zzix();
    }

    protected String zziy() {
        return this.Db.zziy();
    }
}
