package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.internal.zzst.zza;

public class zzsr extends zzj<zzst> {
    public zzsr(Context context, Looper looper, zzf com_google_android_gms_common_internal_zzf, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 39, com_google_android_gms_common_internal_zzf, connectionCallbacks, onConnectionFailedListener);
    }

    protected zzst zzdz(IBinder iBinder) {
        return zza.zzeb(iBinder);
    }

    protected /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzdz(iBinder);
    }

    public String zzjx() {
        return "com.google.android.gms.common.service.START";
    }

    protected String zzjy() {
        return "com.google.android.gms.common.internal.service.ICommonService";
    }
}
