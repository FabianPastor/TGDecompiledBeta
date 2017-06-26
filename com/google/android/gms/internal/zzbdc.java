package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzbx;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class zzbdc<O extends ApiOptions> implements ConnectionCallbacks, OnConnectionFailedListener, zzbbi {
    private final zzbas<O> zzaAK;
    private final zze zzaCy;
    private boolean zzaDA;
    private /* synthetic */ zzbda zzaEm;
    private final Queue<zzbal> zzaEn = new LinkedList();
    private final zzb zzaEo;
    private final zzbbs zzaEp;
    private final Set<zzbau> zzaEq = new HashSet();
    private final Map<zzbdx<?>, zzbee> zzaEr = new HashMap();
    private final int zzaEs;
    private final zzbei zzaEt;
    private ConnectionResult zzaEu = null;

    @WorkerThread
    public zzbdc(zzbda com_google_android_gms_internal_zzbda, GoogleApi<O> googleApi) {
        this.zzaEm = com_google_android_gms_internal_zzbda;
        this.zzaCy = googleApi.zza(com_google_android_gms_internal_zzbda.mHandler.getLooper(), this);
        if (this.zzaCy instanceof zzbx) {
            zzbx com_google_android_gms_common_internal_zzbx = (zzbx) this.zzaCy;
            this.zzaEo = null;
        } else {
            this.zzaEo = this.zzaCy;
        }
        this.zzaAK = googleApi.zzph();
        this.zzaEp = new zzbbs();
        this.zzaEs = googleApi.getInstanceId();
        if (this.zzaCy.zzmv()) {
            this.zzaEt = googleApi.zza(com_google_android_gms_internal_zzbda.mContext, com_google_android_gms_internal_zzbda.mHandler);
        } else {
            this.zzaEt = null;
        }
    }

    @WorkerThread
    private final void zzb(zzbal com_google_android_gms_internal_zzbal) {
        com_google_android_gms_internal_zzbal.zza(this.zzaEp, zzmv());
        try {
            com_google_android_gms_internal_zzbal.zza(this);
        } catch (DeadObjectException e) {
            onConnectionSuspended(1);
            this.zzaCy.disconnect();
        }
    }

    @WorkerThread
    private final void zzi(ConnectionResult connectionResult) {
        for (zzbau zza : this.zzaEq) {
            zza.zza(this.zzaAK, connectionResult);
        }
        this.zzaEq.clear();
    }

    @WorkerThread
    private final void zzqq() {
        zzqt();
        zzi(ConnectionResult.zzazX);
        zzqv();
        for (zzbee com_google_android_gms_internal_zzbee : this.zzaEr.values()) {
            try {
                com_google_android_gms_internal_zzbee.zzaBu.zzb(this.zzaEo, new TaskCompletionSource());
            } catch (DeadObjectException e) {
                onConnectionSuspended(1);
                this.zzaCy.disconnect();
            } catch (RemoteException e2) {
            }
        }
        while (this.zzaCy.isConnected() && !this.zzaEn.isEmpty()) {
            zzb((zzbal) this.zzaEn.remove());
        }
        zzqw();
    }

    @WorkerThread
    private final void zzqr() {
        zzqt();
        this.zzaDA = true;
        this.zzaEp.zzpQ();
        this.zzaEm.mHandler.sendMessageDelayed(Message.obtain(this.zzaEm.mHandler, 9, this.zzaAK), this.zzaEm.zzaDC);
        this.zzaEm.mHandler.sendMessageDelayed(Message.obtain(this.zzaEm.mHandler, 11, this.zzaAK), this.zzaEm.zzaDB);
        this.zzaEm.zzaEg = -1;
    }

    @WorkerThread
    private final void zzqv() {
        if (this.zzaDA) {
            this.zzaEm.mHandler.removeMessages(11, this.zzaAK);
            this.zzaEm.mHandler.removeMessages(9, this.zzaAK);
            this.zzaDA = false;
        }
    }

    private final void zzqw() {
        this.zzaEm.mHandler.removeMessages(12, this.zzaAK);
        this.zzaEm.mHandler.sendMessageDelayed(this.zzaEm.mHandler.obtainMessage(12, this.zzaAK), this.zzaEm.zzaEe);
    }

    @WorkerThread
    public final void connect() {
        zzbo.zza(this.zzaEm.mHandler);
        if (!this.zzaCy.isConnected() && !this.zzaCy.isConnecting()) {
            if (this.zzaCy.zzpe() && this.zzaEm.zzaEg != 0) {
                this.zzaEm.zzaEg = this.zzaEm.zzaBd.isGooglePlayServicesAvailable(this.zzaEm.mContext);
                if (this.zzaEm.zzaEg != 0) {
                    onConnectionFailed(new ConnectionResult(this.zzaEm.zzaEg, null));
                    return;
                }
            }
            Object com_google_android_gms_internal_zzbdg = new zzbdg(this.zzaEm, this.zzaCy, this.zzaAK);
            if (this.zzaCy.zzmv()) {
                this.zzaEt.zza(com_google_android_gms_internal_zzbdg);
            }
            this.zzaCy.zza(com_google_android_gms_internal_zzbdg);
        }
    }

    public final int getInstanceId() {
        return this.zzaEs;
    }

    final boolean isConnected() {
        return this.zzaCy.isConnected();
    }

    public final void onConnected(@Nullable Bundle bundle) {
        if (Looper.myLooper() == this.zzaEm.mHandler.getLooper()) {
            zzqq();
        } else {
            this.zzaEm.mHandler.post(new zzbdd(this));
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @WorkerThread
    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        zzbo.zza(this.zzaEm.mHandler);
        if (this.zzaEt != null) {
            this.zzaEt.zzqI();
        }
        zzqt();
        this.zzaEm.zzaEg = -1;
        zzi(connectionResult);
        if (connectionResult.getErrorCode() == 4) {
            zzt(zzbda.zzaEd);
        } else if (this.zzaEn.isEmpty()) {
            this.zzaEu = connectionResult;
        } else {
            synchronized (zzbda.zzuH) {
                if (this.zzaEm.zzaEj != null && this.zzaEm.zzaEk.contains(this.zzaAK)) {
                    this.zzaEm.zzaEj.zzb(connectionResult, this.zzaEs);
                }
            }
        }
    }

    public final void onConnectionSuspended(int i) {
        if (Looper.myLooper() == this.zzaEm.mHandler.getLooper()) {
            zzqr();
        } else {
            this.zzaEm.mHandler.post(new zzbde(this));
        }
    }

    @WorkerThread
    public final void resume() {
        zzbo.zza(this.zzaEm.mHandler);
        if (this.zzaDA) {
            connect();
        }
    }

    @WorkerThread
    public final void signOut() {
        zzbo.zza(this.zzaEm.mHandler);
        zzt(zzbda.zzaEc);
        this.zzaEp.zzpP();
        for (zzbdx com_google_android_gms_internal_zzbaq : this.zzaEr.keySet()) {
            zza(new zzbaq(com_google_android_gms_internal_zzbaq, new TaskCompletionSource()));
        }
        zzi(new ConnectionResult(4));
        this.zzaCy.disconnect();
    }

    public final void zza(ConnectionResult connectionResult, Api<?> api, boolean z) {
        if (Looper.myLooper() == this.zzaEm.mHandler.getLooper()) {
            onConnectionFailed(connectionResult);
        } else {
            this.zzaEm.mHandler.post(new zzbdf(this, connectionResult));
        }
    }

    @WorkerThread
    public final void zza(zzbal com_google_android_gms_internal_zzbal) {
        zzbo.zza(this.zzaEm.mHandler);
        if (this.zzaCy.isConnected()) {
            zzb(com_google_android_gms_internal_zzbal);
            zzqw();
            return;
        }
        this.zzaEn.add(com_google_android_gms_internal_zzbal);
        if (this.zzaEu == null || !this.zzaEu.hasResolution()) {
            connect();
        } else {
            onConnectionFailed(this.zzaEu);
        }
    }

    @WorkerThread
    public final void zza(zzbau com_google_android_gms_internal_zzbau) {
        zzbo.zza(this.zzaEm.mHandler);
        this.zzaEq.add(com_google_android_gms_internal_zzbau);
    }

    @WorkerThread
    public final void zzh(@NonNull ConnectionResult connectionResult) {
        zzbo.zza(this.zzaEm.mHandler);
        this.zzaCy.disconnect();
        onConnectionFailed(connectionResult);
    }

    public final boolean zzmv() {
        return this.zzaCy.zzmv();
    }

    public final zze zzpJ() {
        return this.zzaCy;
    }

    @WorkerThread
    public final void zzqd() {
        zzbo.zza(this.zzaEm.mHandler);
        if (this.zzaDA) {
            zzqv();
            zzt(this.zzaEm.zzaBd.isGooglePlayServicesAvailable(this.zzaEm.mContext) == 18 ? new Status(8, "Connection timed out while waiting for Google Play services update to complete.") : new Status(8, "API failed to connect while resuming due to an unknown error."));
            this.zzaCy.disconnect();
        }
    }

    public final Map<zzbdx<?>, zzbee> zzqs() {
        return this.zzaEr;
    }

    @WorkerThread
    public final void zzqt() {
        zzbo.zza(this.zzaEm.mHandler);
        this.zzaEu = null;
    }

    @WorkerThread
    public final ConnectionResult zzqu() {
        zzbo.zza(this.zzaEm.mHandler);
        return this.zzaEu;
    }

    @WorkerThread
    public final void zzqx() {
        zzbo.zza(this.zzaEm.mHandler);
        if (!this.zzaCy.isConnected() || this.zzaEr.size() != 0) {
            return;
        }
        if (this.zzaEp.zzpO()) {
            zzqw();
        } else {
            this.zzaCy.disconnect();
        }
    }

    final zzctj zzqy() {
        return this.zzaEt == null ? null : this.zzaEt.zzqy();
    }

    @WorkerThread
    public final void zzt(Status status) {
        zzbo.zza(this.zzaEm.mHandler);
        for (zzbal zzp : this.zzaEn) {
            zzp.zzp(status);
        }
        this.zzaEn.clear();
    }
}
