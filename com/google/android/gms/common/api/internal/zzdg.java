package com.google.android.gms.common.api.internal;

import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.common.internal.zzbq;
import java.lang.ref.WeakReference;

public final class zzdg<R extends Result> extends TransformedResult<R> implements ResultCallback<R> {
    private final Object zzfou;
    private final WeakReference<GoogleApiClient> zzfow;
    private ResultTransform<? super R, ? extends Result> zzfux;
    private zzdg<? extends Result> zzfuy;
    private volatile ResultCallbacks<? super R> zzfuz;
    private PendingResult<R> zzfva;
    private Status zzfvb;
    private final zzdi zzfvc;
    private boolean zzfvd;

    private final void zzajr() {
        if (this.zzfux != null || this.zzfuz != null) {
            GoogleApiClient googleApiClient = (GoogleApiClient) this.zzfow.get();
            if (!(this.zzfvd || this.zzfux == null || googleApiClient == null)) {
                googleApiClient.zza(this);
                this.zzfvd = true;
            }
            if (this.zzfvb != null) {
                zzx(this.zzfvb);
            } else if (this.zzfva != null) {
                this.zzfva.setResultCallback(this);
            }
        }
    }

    private final boolean zzajt() {
        return (this.zzfuz == null || ((GoogleApiClient) this.zzfow.get()) == null) ? false : true;
    }

    private static void zzd(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (Throwable e) {
                String valueOf = String.valueOf(result);
                Log.w("TransformedResultImpl", new StringBuilder(String.valueOf(valueOf).length() + 18).append("Unable to release ").append(valueOf).toString(), e);
            }
        }
    }

    private final void zzd(Status status) {
        synchronized (this.zzfou) {
            this.zzfvb = status;
            zzx(this.zzfvb);
        }
    }

    private final void zzx(Status status) {
        synchronized (this.zzfou) {
            if (this.zzfux != null) {
                Status onFailure = this.zzfux.onFailure(status);
                zzbq.checkNotNull(onFailure, "onFailure must not return null");
                this.zzfuy.zzd(onFailure);
            } else if (zzajt()) {
                this.zzfuz.onFailure(status);
            }
        }
    }

    public final void onResult(R r) {
        synchronized (this.zzfou) {
            if (!r.getStatus().isSuccess()) {
                zzd(r.getStatus());
                zzd((Result) r);
            } else if (this.zzfux != null) {
                zzcs.zzaip().submit(new zzdh(this, r));
            } else if (zzajt()) {
                this.zzfuz.onSuccess(r);
            }
        }
    }

    public final void zza(PendingResult<?> pendingResult) {
        synchronized (this.zzfou) {
            this.zzfva = pendingResult;
            zzajr();
        }
    }

    final void zzajs() {
        this.zzfuz = null;
    }
}
