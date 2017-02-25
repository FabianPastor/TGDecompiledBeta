package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.internal.zzacl.zza;

public class zzacj extends zzl<zzacl> {
    public zzacj(Context context, Looper looper, zzg com_google_android_gms_common_internal_zzg, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 39, com_google_android_gms_common_internal_zzg, connectionCallbacks, onConnectionFailedListener);
    }

    protected zzacl zzbz(IBinder iBinder) {
        return zza.zzbB(iBinder);
    }

    protected String zzeA() {
        return "com.google.android.gms.common.internal.service.ICommonService";
    }

    public String zzez() {
        return "com.google.android.gms.common.service.START";
    }

    protected /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzbz(iBinder);
    }
}
