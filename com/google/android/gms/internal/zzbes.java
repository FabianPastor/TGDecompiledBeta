package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
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
import com.google.android.gms.common.internal.zzbo;
import java.lang.ref.WeakReference;

public final class zzbes<R extends Result> extends TransformedResult<R> implements ResultCallback<R> {
    private final Object zzaBW = new Object();
    private final WeakReference<GoogleApiClient> zzaBY;
    private ResultTransform<? super R, ? extends Result> zzaFa = null;
    private zzbes<? extends Result> zzaFb = null;
    private volatile ResultCallbacks<? super R> zzaFc = null;
    private PendingResult<R> zzaFd = null;
    private Status zzaFe = null;
    private final zzbeu zzaFf;
    private boolean zzaFg = false;

    public zzbes(WeakReference<GoogleApiClient> weakReference) {
        zzbo.zzb((Object) weakReference, (Object) "GoogleApiClient reference must not be null");
        this.zzaBY = weakReference;
        GoogleApiClient googleApiClient = (GoogleApiClient) this.zzaBY.get();
        this.zzaFf = new zzbeu(this, googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
    }

    private static void zzc(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (Throwable e) {
                String valueOf = String.valueOf(result);
                Log.w("TransformedResultImpl", new StringBuilder(String.valueOf(valueOf).length() + 18).append("Unable to release ").append(valueOf).toString(), e);
            }
        }
    }

    private final void zzqJ() {
        if (this.zzaFa != null || this.zzaFc != null) {
            GoogleApiClient googleApiClient = (GoogleApiClient) this.zzaBY.get();
            if (!(this.zzaFg || this.zzaFa == null || googleApiClient == null)) {
                googleApiClient.zza(this);
                this.zzaFg = true;
            }
            if (this.zzaFe != null) {
                zzw(this.zzaFe);
            } else if (this.zzaFd != null) {
                this.zzaFd.setResultCallback(this);
            }
        }
    }

    private final boolean zzqL() {
        return (this.zzaFc == null || ((GoogleApiClient) this.zzaBY.get()) == null) ? false : true;
    }

    private final void zzv(Status status) {
        synchronized (this.zzaBW) {
            this.zzaFe = status;
            zzw(this.zzaFe);
        }
    }

    private final void zzw(Status status) {
        synchronized (this.zzaBW) {
            if (this.zzaFa != null) {
                Object onFailure = this.zzaFa.onFailure(status);
                zzbo.zzb(onFailure, (Object) "onFailure must not return null");
                this.zzaFb.zzv(onFailure);
            } else if (zzqL()) {
                this.zzaFc.onFailure(status);
            }
        }
    }

    public final void andFinally(@NonNull ResultCallbacks<? super R> resultCallbacks) {
        boolean z = true;
        synchronized (this.zzaBW) {
            zzbo.zza(this.zzaFc == null, (Object) "Cannot call andFinally() twice.");
            if (this.zzaFa != null) {
                z = false;
            }
            zzbo.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.zzaFc = resultCallbacks;
            zzqJ();
        }
    }

    public final void onResult(R r) {
        synchronized (this.zzaBW) {
            if (!r.getStatus().isSuccess()) {
                zzv(r.getStatus());
                zzc((Result) r);
            } else if (this.zzaFa != null) {
                zzbeg.zzqj().submit(new zzbet(this, r));
            } else if (zzqL()) {
                this.zzaFc.onSuccess(r);
            }
        }
    }

    @NonNull
    public final <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult com_google_android_gms_internal_zzbes;
        boolean z = true;
        synchronized (this.zzaBW) {
            zzbo.zza(this.zzaFa == null, (Object) "Cannot call then() twice.");
            if (this.zzaFc != null) {
                z = false;
            }
            zzbo.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.zzaFa = resultTransform;
            com_google_android_gms_internal_zzbes = new zzbes(this.zzaBY);
            this.zzaFb = com_google_android_gms_internal_zzbes;
            zzqJ();
        }
        return com_google_android_gms_internal_zzbes;
    }

    public final void zza(PendingResult<?> pendingResult) {
        synchronized (this.zzaBW) {
            this.zzaFd = pendingResult;
            zzqJ();
        }
    }

    final void zzqK() {
        this.zzaFc = null;
    }
}
