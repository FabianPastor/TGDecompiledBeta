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
import com.google.android.gms.auth.api.signin.internal.zzl;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzaf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzr;
import java.util.HashSet;
import java.util.Set;

public class zzabj extends zzaxr implements ConnectionCallbacks, OnConnectionFailedListener {
    private static com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> zzaBH = zzaxm.zzahd;
    private final Context mContext;
    private final Handler mHandler;
    private final boolean zzaBI;
    private zza zzaBJ;
    private Set<Scope> zzajm;
    private final com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> zzaxu;
    private zzaxn zzazS;
    private zzg zzazs;

    @WorkerThread
    public interface zza {
        void zzb(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set);

        void zzi(ConnectionResult connectionResult);
    }

    @WorkerThread
    public zzabj(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        GoogleSignInOptions zzrd = zzl.zzaa(this.mContext).zzrd();
        this.zzajm = zzrd == null ? new HashSet() : new HashSet(zzrd.zzqJ());
        this.zzazs = new zzg(null, this.zzajm, null, 0, null, null, null, zzaxo.zzbCg);
        this.zzaxu = zzaBH;
        this.zzaBI = true;
    }

    @WorkerThread
    public zzabj(Context context, Handler handler, zzg com_google_android_gms_common_internal_zzg, com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo) {
        this.mContext = context;
        this.mHandler = handler;
        this.zzazs = com_google_android_gms_common_internal_zzg;
        this.zzajm = com_google_android_gms_common_internal_zzg.zzxe();
        this.zzaxu = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo;
        this.zzaBI = false;
    }

    @WorkerThread
    private void zzc(zzayb com_google_android_gms_internal_zzayb) {
        ConnectionResult zzxA = com_google_android_gms_internal_zzayb.zzxA();
        if (zzxA.isSuccess()) {
            zzaf zzOp = com_google_android_gms_internal_zzayb.zzOp();
            ConnectionResult zzxA2 = zzOp.zzxA();
            if (zzxA2.isSuccess()) {
                this.zzaBJ.zzb(zzOp.zzxz(), this.zzajm);
            } else {
                String valueOf = String.valueOf(zzxA2);
                Log.wtf("SignInCoordinator", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                this.zzaBJ.zzi(zzxA2);
                this.zzazS.disconnect();
                return;
            }
        }
        this.zzaBJ.zzi(zzxA);
        this.zzazS.disconnect();
    }

    @WorkerThread
    public void onConnected(@Nullable Bundle bundle) {
        this.zzazS.zza(this);
    }

    @WorkerThread
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.zzaBJ.zzi(connectionResult);
    }

    @WorkerThread
    public void onConnectionSuspended(int i) {
        this.zzazS.disconnect();
    }

    @WorkerThread
    public void zza(zza com_google_android_gms_internal_zzabj_zza) {
        if (this.zzazS != null) {
            this.zzazS.disconnect();
        }
        if (this.zzaBI) {
            GoogleSignInOptions zzrd = zzl.zzaa(this.mContext).zzrd();
            this.zzajm = zzrd == null ? new HashSet() : new HashSet(zzrd.zzqJ());
            this.zzazs = new zzg(null, this.zzajm, null, 0, null, null, null, zzaxo.zzbCg);
        }
        this.zzazS = (zzaxn) this.zzaxu.zza(this.mContext, this.mHandler.getLooper(), this.zzazs, this.zzazs.zzxk(), this, this);
        this.zzaBJ = com_google_android_gms_internal_zzabj_zza;
        this.zzazS.connect();
    }

    @BinderThread
    public void zzb(final zzayb com_google_android_gms_internal_zzayb) {
        this.mHandler.post(new Runnable(this) {
            final /* synthetic */ zzabj zzaBK;

            public void run() {
                this.zzaBK.zzc(com_google_android_gms_internal_zzayb);
            }
        });
    }

    public void zzwr() {
        this.zzazS.disconnect();
    }
}
