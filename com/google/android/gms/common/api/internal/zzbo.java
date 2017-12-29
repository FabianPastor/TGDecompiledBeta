package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzbz;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.internal.zzcxd;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class zzbo<O extends ApiOptions> implements ConnectionCallbacks, OnConnectionFailedListener, zzu {
    private final zzh<O> zzfmf;
    private final zze zzfpv;
    private boolean zzfrw;
    final /* synthetic */ zzbm zzfti;
    private final Queue<zza> zzftj = new LinkedList();
    private final zzb zzftk;
    private final zzae zzftl;
    private final Set<zzj> zzftm = new HashSet();
    private final Map<zzck<?>, zzcr> zzftn = new HashMap();
    private final int zzfto;
    private final zzcv zzftp;
    private ConnectionResult zzftq = null;

    public zzbo(zzbm com_google_android_gms_common_api_internal_zzbm, GoogleApi<O> googleApi) {
        this.zzfti = com_google_android_gms_common_api_internal_zzbm;
        this.zzfpv = googleApi.zza(com_google_android_gms_common_api_internal_zzbm.mHandler.getLooper(), this);
        if (this.zzfpv instanceof zzbz) {
            this.zzftk = zzbz.zzals();
        } else {
            this.zzftk = this.zzfpv;
        }
        this.zzfmf = googleApi.zzagn();
        this.zzftl = new zzae();
        this.zzfto = googleApi.getInstanceId();
        if (this.zzfpv.zzaay()) {
            this.zzftp = googleApi.zza(com_google_android_gms_common_api_internal_zzbm.mContext, com_google_android_gms_common_api_internal_zzbm.mHandler);
        } else {
            this.zzftp = null;
        }
    }

    private final void zzaiw() {
        zzaiz();
        zzi(ConnectionResult.zzfkr);
        zzajb();
        for (zzcr com_google_android_gms_common_api_internal_zzcr : this.zzftn.values()) {
            try {
                com_google_android_gms_common_api_internal_zzcr.zzfnq.zzb(this.zzftk, new TaskCompletionSource());
            } catch (DeadObjectException e) {
                onConnectionSuspended(1);
                this.zzfpv.disconnect();
            } catch (RemoteException e2) {
            }
        }
        while (this.zzfpv.isConnected() && !this.zzftj.isEmpty()) {
            zzb((zza) this.zzftj.remove());
        }
        zzajc();
    }

    private final void zzaix() {
        zzaiz();
        this.zzfrw = true;
        this.zzftl.zzahw();
        this.zzfti.mHandler.sendMessageDelayed(Message.obtain(this.zzfti.mHandler, 9, this.zzfmf), this.zzfti.zzfry);
        this.zzfti.mHandler.sendMessageDelayed(Message.obtain(this.zzfti.mHandler, 11, this.zzfmf), this.zzfti.zzfrx);
        this.zzfti.zzftc = -1;
    }

    private final void zzajb() {
        if (this.zzfrw) {
            this.zzfti.mHandler.removeMessages(11, this.zzfmf);
            this.zzfti.mHandler.removeMessages(9, this.zzfmf);
            this.zzfrw = false;
        }
    }

    private final void zzajc() {
        this.zzfti.mHandler.removeMessages(12, this.zzfmf);
        this.zzfti.mHandler.sendMessageDelayed(this.zzfti.mHandler.obtainMessage(12, this.zzfmf), this.zzfti.zzfta);
    }

    private final void zzb(zza com_google_android_gms_common_api_internal_zza) {
        com_google_android_gms_common_api_internal_zza.zza(this.zzftl, zzaay());
        try {
            com_google_android_gms_common_api_internal_zza.zza(this);
        } catch (DeadObjectException e) {
            onConnectionSuspended(1);
            this.zzfpv.disconnect();
        }
    }

    private final void zzi(ConnectionResult connectionResult) {
        for (zzj com_google_android_gms_common_api_internal_zzj : this.zzftm) {
            String str = null;
            if (connectionResult == ConnectionResult.zzfkr) {
                str = this.zzfpv.zzagi();
            }
            com_google_android_gms_common_api_internal_zzj.zza(this.zzfmf, connectionResult, str);
        }
        this.zzftm.clear();
    }

    public final void connect() {
        zzbq.zza(this.zzfti.mHandler);
        if (!this.zzfpv.isConnected() && !this.zzfpv.isConnecting()) {
            if (this.zzfpv.zzagg() && this.zzfti.zzftc != 0) {
                this.zzfti.zzftc = this.zzfti.zzfmy.isGooglePlayServicesAvailable(this.zzfti.mContext);
                if (this.zzfti.zzftc != 0) {
                    onConnectionFailed(new ConnectionResult(this.zzfti.zzftc, null));
                    return;
                }
            }
            zzj com_google_android_gms_common_api_internal_zzbu = new zzbu(this.zzfti, this.zzfpv, this.zzfmf);
            if (this.zzfpv.zzaay()) {
                this.zzftp.zza((zzcy) com_google_android_gms_common_api_internal_zzbu);
            }
            this.zzfpv.zza(com_google_android_gms_common_api_internal_zzbu);
        }
    }

    public final int getInstanceId() {
        return this.zzfto;
    }

    final boolean isConnected() {
        return this.zzfpv.isConnected();
    }

    public final void onConnected(Bundle bundle) {
        if (Looper.myLooper() == this.zzfti.mHandler.getLooper()) {
            zzaiw();
        } else {
            this.zzfti.mHandler.post(new zzbp(this));
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onConnectionFailed(ConnectionResult connectionResult) {
        zzbq.zza(this.zzfti.mHandler);
        if (this.zzftp != null) {
            this.zzftp.zzajq();
        }
        zzaiz();
        this.zzfti.zzftc = -1;
        zzi(connectionResult);
        if (connectionResult.getErrorCode() == 4) {
            zzw(zzbm.zzfsz);
        } else if (this.zzftj.isEmpty()) {
            this.zzftq = connectionResult;
        } else {
            synchronized (zzbm.sLock) {
                if (this.zzfti.zzftf != null && this.zzfti.zzftg.contains(this.zzfmf)) {
                    this.zzfti.zzftf.zzb(connectionResult, this.zzfto);
                }
            }
        }
    }

    public final void onConnectionSuspended(int i) {
        if (Looper.myLooper() == this.zzfti.mHandler.getLooper()) {
            zzaix();
        } else {
            this.zzfti.mHandler.post(new zzbq(this));
        }
    }

    public final void resume() {
        zzbq.zza(this.zzfti.mHandler);
        if (this.zzfrw) {
            connect();
        }
    }

    public final void signOut() {
        zzbq.zza(this.zzfti.mHandler);
        zzw(zzbm.zzfsy);
        this.zzftl.zzahv();
        for (zzck com_google_android_gms_common_api_internal_zzf : (zzck[]) this.zzftn.keySet().toArray(new zzck[this.zzftn.size()])) {
            zza(new zzf(com_google_android_gms_common_api_internal_zzf, new TaskCompletionSource()));
        }
        zzi(new ConnectionResult(4));
        if (this.zzfpv.isConnected()) {
            this.zzfpv.zza(new zzbs(this));
        }
    }

    public final void zza(ConnectionResult connectionResult, Api<?> api, boolean z) {
        if (Looper.myLooper() == this.zzfti.mHandler.getLooper()) {
            onConnectionFailed(connectionResult);
        } else {
            this.zzfti.mHandler.post(new zzbr(this, connectionResult));
        }
    }

    public final void zza(zza com_google_android_gms_common_api_internal_zza) {
        zzbq.zza(this.zzfti.mHandler);
        if (this.zzfpv.isConnected()) {
            zzb(com_google_android_gms_common_api_internal_zza);
            zzajc();
            return;
        }
        this.zzftj.add(com_google_android_gms_common_api_internal_zza);
        if (this.zzftq == null || !this.zzftq.hasResolution()) {
            connect();
        } else {
            onConnectionFailed(this.zzftq);
        }
    }

    public final void zza(zzj com_google_android_gms_common_api_internal_zzj) {
        zzbq.zza(this.zzfti.mHandler);
        this.zzftm.add(com_google_android_gms_common_api_internal_zzj);
    }

    public final boolean zzaay() {
        return this.zzfpv.zzaay();
    }

    public final zze zzahp() {
        return this.zzfpv;
    }

    public final void zzaij() {
        zzbq.zza(this.zzfti.mHandler);
        if (this.zzfrw) {
            zzajb();
            zzw(this.zzfti.zzfmy.isGooglePlayServicesAvailable(this.zzfti.mContext) == 18 ? new Status(8, "Connection timed out while waiting for Google Play services update to complete.") : new Status(8, "API failed to connect while resuming due to an unknown error."));
            this.zzfpv.disconnect();
        }
    }

    public final Map<zzck<?>, zzcr> zzaiy() {
        return this.zzftn;
    }

    public final void zzaiz() {
        zzbq.zza(this.zzfti.mHandler);
        this.zzftq = null;
    }

    public final ConnectionResult zzaja() {
        zzbq.zza(this.zzfti.mHandler);
        return this.zzftq;
    }

    public final void zzajd() {
        zzbq.zza(this.zzfti.mHandler);
        if (!this.zzfpv.isConnected() || this.zzftn.size() != 0) {
            return;
        }
        if (this.zzftl.zzahu()) {
            zzajc();
        } else {
            this.zzfpv.disconnect();
        }
    }

    final zzcxd zzaje() {
        return this.zzftp == null ? null : this.zzftp.zzaje();
    }

    public final void zzh(ConnectionResult connectionResult) {
        zzbq.zza(this.zzfti.mHandler);
        this.zzfpv.disconnect();
        onConnectionFailed(connectionResult);
    }

    public final void zzw(Status status) {
        zzbq.zza(this.zzfti.mHandler);
        for (zza zzs : this.zzftj) {
            zzs.zzs(status);
        }
        this.zzftj.clear();
    }
}
