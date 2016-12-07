package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.internal.zzsd.zza;

public class zzsb extends zzl<zzsd> {
    public zzsb(Context context, Looper looper, zzh com_google_android_gms_common_internal_zzh, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 39, com_google_android_gms_common_internal_zzh, connectionCallbacks, onConnectionFailedListener);
    }

    protected zzsd zzea(IBinder iBinder) {
        return zza.zzec(iBinder);
    }

    protected /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzea(iBinder);
    }

    public String zzix() {
        return "com.google.android.gms.common.service.START";
    }

    protected String zziy() {
        return "com.google.android.gms.common.internal.service.ICommonService";
    }
}
