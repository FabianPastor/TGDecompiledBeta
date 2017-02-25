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
import com.google.android.gms.auth.api.signin.internal.zzn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzaf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzr;
import java.util.HashSet;
import java.util.Set;

public class zzabr extends zzbam implements ConnectionCallbacks, OnConnectionFailedListener {
    private static com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> zzaDg = zzbah.zzaie;
    private final Context mContext;
    private final Handler mHandler;
    private zzg zzaAL;
    private zzbai zzaBs;
    private final boolean zzaDh;
    private zza zzaDi;
    private Set<Scope> zzakq;
    private final com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> zzayH;

    @WorkerThread
    public interface zza {
        void zzb(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set);

        void zzi(ConnectionResult connectionResult);
    }

    @WorkerThread
    public zzabr(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.zzayH = zzaDg;
        this.zzaDh = true;
    }

    @WorkerThread
    public zzabr(Context context, Handler handler, zzg com_google_android_gms_common_internal_zzg, com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj) {
        this.mContext = context;
        this.mHandler = handler;
        this.zzaAL = com_google_android_gms_common_internal_zzg;
        this.zzakq = com_google_android_gms_common_internal_zzg.zzxL();
        this.zzayH = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj;
        this.zzaDh = false;
    }

    @WorkerThread
    private void zzc(zzbaw com_google_android_gms_internal_zzbaw) {
        ConnectionResult zzyh = com_google_android_gms_internal_zzbaw.zzyh();
        if (zzyh.isSuccess()) {
            zzaf zzPT = com_google_android_gms_internal_zzbaw.zzPT();
            ConnectionResult zzyh2 = zzPT.zzyh();
            if (zzyh2.isSuccess()) {
                this.zzaDi.zzb(zzPT.zzyg(), this.zzakq);
            } else {
                String valueOf = String.valueOf(zzyh2);
                Log.wtf("SignInCoordinator", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                this.zzaDi.zzi(zzyh2);
                this.zzaBs.disconnect();
                return;
            }
        }
        this.zzaDi.zzi(zzyh);
        this.zzaBs.disconnect();
    }

    @WorkerThread
    public void onConnected(@Nullable Bundle bundle) {
        this.zzaBs.zza(this);
    }

    @WorkerThread
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.zzaDi.zzi(connectionResult);
    }

    @WorkerThread
    public void onConnectionSuspended(int i) {
        this.zzaBs.disconnect();
    }

    @WorkerThread
    public void zza(zza com_google_android_gms_internal_zzabr_zza) {
        if (this.zzaBs != null) {
            this.zzaBs.disconnect();
        }
        if (this.zzaDh) {
            GoogleSignInOptions zzrC = zzn.zzas(this.mContext).zzrC();
            this.zzakq = zzrC == null ? new HashSet() : new HashSet(zzrC.zzrj());
            this.zzaAL = new zzg(null, this.zzakq, null, 0, null, null, null, zzbaj.zzbEm);
        }
        this.zzaBs = (zzbai) this.zzayH.zza(this.mContext, this.mHandler.getLooper(), this.zzaAL, this.zzaAL.zzxR(), this, this);
        this.zzaDi = com_google_android_gms_internal_zzabr_zza;
        this.zzaBs.connect();
    }

    @BinderThread
    public void zzb(final zzbaw com_google_android_gms_internal_zzbaw) {
        this.mHandler.post(new Runnable(this) {
            final /* synthetic */ zzabr zzaDj;

            public void run() {
                this.zzaDj.zzc(com_google_android_gms_internal_zzbaw);
            }
        });
    }

    public zzbai zzwO() {
        return this.zzaBs;
    }

    public void zzwY() {
        this.zzaBs.disconnect();
    }
}
