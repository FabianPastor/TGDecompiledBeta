package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.zzy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzbr;
import com.google.android.gms.common.internal.zzq;
import java.util.HashSet;
import java.util.Set;

public final class zzbej extends zzctp implements ConnectionCallbacks, OnConnectionFailedListener {
    private static zza<? extends zzctk, zzctl> zzaEV = zzctg.zzajS;
    private final Context mContext;
    private final Handler mHandler;
    private final zza<? extends zzctk, zzctl> zzaAx;
    private zzq zzaCA;
    private zzctk zzaDh;
    private final boolean zzaEW;
    private zzbel zzaEX;
    private Set<Scope> zzame;

    @WorkerThread
    public zzbej(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.zzaAx = zzaEV;
        this.zzaEW = true;
    }

    @WorkerThread
    public zzbej(Context context, Handler handler, @NonNull zzq com_google_android_gms_common_internal_zzq, zza<? extends zzctk, zzctl> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl) {
        this.mContext = context;
        this.mHandler = handler;
        this.zzaCA = (zzq) zzbo.zzb((Object) com_google_android_gms_common_internal_zzq, (Object) "ClientSettings must not be null");
        this.zzame = com_google_android_gms_common_internal_zzq.zzrn();
        this.zzaAx = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl;
        this.zzaEW = false;
    }

    @WorkerThread
    private final void zzc(zzctx com_google_android_gms_internal_zzctx) {
        ConnectionResult zzpz = com_google_android_gms_internal_zzctx.zzpz();
        if (zzpz.isSuccess()) {
            zzbr zzAx = com_google_android_gms_internal_zzctx.zzAx();
            ConnectionResult zzpz2 = zzAx.zzpz();
            if (zzpz2.isSuccess()) {
                this.zzaEX.zzb(zzAx.zzrH(), this.zzame);
            } else {
                String valueOf = String.valueOf(zzpz2);
                Log.wtf("SignInCoordinator", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                this.zzaEX.zzh(zzpz2);
                this.zzaDh.disconnect();
                return;
            }
        }
        this.zzaEX.zzh(zzpz);
        this.zzaDh.disconnect();
    }

    @WorkerThread
    public final void onConnected(@Nullable Bundle bundle) {
        this.zzaDh.zza(this);
    }

    @WorkerThread
    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.zzaEX.zzh(connectionResult);
    }

    @WorkerThread
    public final void onConnectionSuspended(int i) {
        this.zzaDh.disconnect();
    }

    @WorkerThread
    public final void zza(zzbel com_google_android_gms_internal_zzbel) {
        if (this.zzaDh != null) {
            this.zzaDh.disconnect();
        }
        if (this.zzaEW) {
            GoogleSignInOptions zzmO = zzy.zzaj(this.mContext).zzmO();
            this.zzame = zzmO == null ? new HashSet() : new HashSet(zzmO.zzmA());
            this.zzaCA = new zzq(null, this.zzame, null, 0, null, null, null, zzctl.zzbCM);
        }
        this.zzaCA.zzc(Integer.valueOf(System.identityHashCode(this)));
        this.zzaDh = (zzctk) this.zzaAx.zza(this.mContext, this.mHandler.getLooper(), this.zzaCA, this.zzaCA.zzrt(), this, this);
        this.zzaEX = com_google_android_gms_internal_zzbel;
        this.zzaDh.connect();
    }

    @BinderThread
    public final void zzb(zzctx com_google_android_gms_internal_zzctx) {
        this.mHandler.post(new zzbek(this, com_google_android_gms_internal_zzctx));
    }

    public final void zzqI() {
        if (this.zzaDh != null) {
            this.zzaDh.disconnect();
        }
    }

    public final zzctk zzqy() {
        return this.zzaDh;
    }
}
