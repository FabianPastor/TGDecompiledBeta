package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzbt;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzcxa;
import com.google.android.gms.internal.zzcxd;
import com.google.android.gms.internal.zzcxe;
import com.google.android.gms.internal.zzcxi;
import com.google.android.gms.internal.zzcxq;
import java.util.Set;

public final class zzcv extends zzcxi implements ConnectionCallbacks, OnConnectionFailedListener {
    private static zza<? extends zzcxd, zzcxe> zzfut = zzcxa.zzebg;
    private final Context mContext;
    private final Handler mHandler;
    private Set<Scope> zzehs;
    private final zza<? extends zzcxd, zzcxe> zzfls;
    private zzr zzfpx;
    private zzcxd zzfrd;
    private zzcy zzfuu;

    public zzcv(Context context, Handler handler, zzr com_google_android_gms_common_internal_zzr) {
        this(context, handler, com_google_android_gms_common_internal_zzr, zzfut);
    }

    public zzcv(Context context, Handler handler, zzr com_google_android_gms_common_internal_zzr, zza<? extends zzcxd, zzcxe> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzcxd__com_google_android_gms_internal_zzcxe) {
        this.mContext = context;
        this.mHandler = handler;
        this.zzfpx = (zzr) zzbq.checkNotNull(com_google_android_gms_common_internal_zzr, "ClientSettings must not be null");
        this.zzehs = com_google_android_gms_common_internal_zzr.zzakv();
        this.zzfls = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzcxd__com_google_android_gms_internal_zzcxe;
    }

    private final void zzc(zzcxq com_google_android_gms_internal_zzcxq) {
        ConnectionResult zzahf = com_google_android_gms_internal_zzcxq.zzahf();
        if (zzahf.isSuccess()) {
            zzbt zzbdi = com_google_android_gms_internal_zzcxq.zzbdi();
            ConnectionResult zzahf2 = zzbdi.zzahf();
            if (zzahf2.isSuccess()) {
                this.zzfuu.zzb(zzbdi.zzalp(), this.zzehs);
            } else {
                String valueOf = String.valueOf(zzahf2);
                Log.wtf("SignInCoordinator", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                this.zzfuu.zzh(zzahf2);
                this.zzfrd.disconnect();
                return;
            }
        }
        this.zzfuu.zzh(zzahf);
        this.zzfrd.disconnect();
    }

    public final void onConnected(Bundle bundle) {
        this.zzfrd.zza(this);
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzfuu.zzh(connectionResult);
    }

    public final void onConnectionSuspended(int i) {
        this.zzfrd.disconnect();
    }

    public final void zza(zzcy com_google_android_gms_common_api_internal_zzcy) {
        if (this.zzfrd != null) {
            this.zzfrd.disconnect();
        }
        this.zzfpx.zzc(Integer.valueOf(System.identityHashCode(this)));
        this.zzfrd = (zzcxd) this.zzfls.zza(this.mContext, this.mHandler.getLooper(), this.zzfpx, this.zzfpx.zzalb(), this, this);
        this.zzfuu = com_google_android_gms_common_api_internal_zzcy;
        if (this.zzehs == null || this.zzehs.isEmpty()) {
            this.mHandler.post(new zzcw(this));
        } else {
            this.zzfrd.connect();
        }
    }

    public final zzcxd zzaje() {
        return this.zzfrd;
    }

    public final void zzajq() {
        if (this.zzfrd != null) {
            this.zzfrd.disconnect();
        }
    }

    public final void zzb(zzcxq com_google_android_gms_internal_zzcxq) {
        this.mHandler.post(new zzcx(this, com_google_android_gms_internal_zzcxq));
    }
}
