package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
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
import com.google.android.gms.common.internal.zzac;
import java.lang.ref.WeakReference;

public class zzabp<R extends Result> extends TransformedResult<R> implements ResultCallback<R> {
    private ResultTransform<? super R, ? extends Result> zzaBM = null;
    private zzabp<? extends Result> zzaBN = null;
    private volatile ResultCallbacks<? super R> zzaBO = null;
    private PendingResult<R> zzaBP = null;
    private Status zzaBQ = null;
    private final zza zzaBR;
    private boolean zzaBS = false;
    private final Object zzayO = new Object();
    private final WeakReference<GoogleApiClient> zzayQ;

    private final class zza extends Handler {
        final /* synthetic */ zzabp zzaBU;

        public zza(zzabp com_google_android_gms_internal_zzabp, Looper looper) {
            this.zzaBU = com_google_android_gms_internal_zzabp;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    PendingResult pendingResult = (PendingResult) message.obj;
                    synchronized (this.zzaBU.zzayO) {
                        if (pendingResult == null) {
                            this.zzaBU.zzaBN.zzD(new Status(13, "Transform returned null"));
                        } else if (pendingResult instanceof zzabh) {
                            this.zzaBU.zzaBN.zzD(((zzabh) pendingResult).getStatus());
                        } else {
                            this.zzaBU.zzaBN.zza(pendingResult);
                        }
                    }
                    return;
                case 1:
                    RuntimeException runtimeException = (RuntimeException) message.obj;
                    String str = "TransformedResultImpl";
                    String str2 = "Runtime exception on the transformation worker thread: ";
                    String valueOf = String.valueOf(runtimeException.getMessage());
                    Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                    throw runtimeException;
                default:
                    Log.e("TransformedResultImpl", "TransformationResultHandler received unknown message type: " + message.what);
                    return;
            }
        }
    }

    public zzabp(WeakReference<GoogleApiClient> weakReference) {
        zzac.zzb((Object) weakReference, (Object) "GoogleApiClient reference must not be null");
        this.zzayQ = weakReference;
        GoogleApiClient googleApiClient = (GoogleApiClient) this.zzayQ.get();
        this.zzaBR = new zza(this, googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
    }

    private void zzD(Status status) {
        synchronized (this.zzayO) {
            this.zzaBQ = status;
            zzE(this.zzaBQ);
        }
    }

    private void zzE(Status status) {
        synchronized (this.zzayO) {
            if (this.zzaBM != null) {
                Object onFailure = this.zzaBM.onFailure(status);
                zzac.zzb(onFailure, (Object) "onFailure must not return null");
                this.zzaBN.zzD(onFailure);
            } else if (zzwv()) {
                this.zzaBO.onFailure(status);
            }
        }
    }

    private void zzd(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (Throwable e) {
                String valueOf = String.valueOf(result);
                Log.w("TransformedResultImpl", new StringBuilder(String.valueOf(valueOf).length() + 18).append("Unable to release ").append(valueOf).toString(), e);
            }
        }
    }

    private void zzwt() {
        if (this.zzaBM != null || this.zzaBO != null) {
            GoogleApiClient googleApiClient = (GoogleApiClient) this.zzayQ.get();
            if (!(this.zzaBS || this.zzaBM == null || googleApiClient == null)) {
                googleApiClient.zza(this);
                this.zzaBS = true;
            }
            if (this.zzaBQ != null) {
                zzE(this.zzaBQ);
            } else if (this.zzaBP != null) {
                this.zzaBP.setResultCallback(this);
            }
        }
    }

    private boolean zzwv() {
        return (this.zzaBO == null || ((GoogleApiClient) this.zzayQ.get()) == null) ? false : true;
    }

    public void andFinally(@NonNull ResultCallbacks<? super R> resultCallbacks) {
        boolean z = true;
        synchronized (this.zzayO) {
            zzac.zza(this.zzaBO == null, (Object) "Cannot call andFinally() twice.");
            if (this.zzaBM != null) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.zzaBO = resultCallbacks;
            zzwt();
        }
    }

    public void onResult(final R r) {
        synchronized (this.zzayO) {
            if (!r.getStatus().isSuccess()) {
                zzD(r.getStatus());
                zzd((Result) r);
            } else if (this.zzaBM != null) {
                zzabg.zzvR().submit(new Runnable(this) {
                    final /* synthetic */ zzabp zzaBU;

                    @WorkerThread
                    public void run() {
                        GoogleApiClient googleApiClient;
                        try {
                            zzzx.zzayN.set(Boolean.valueOf(true));
                            this.zzaBU.zzaBR.sendMessage(this.zzaBU.zzaBR.obtainMessage(0, this.zzaBU.zzaBM.onSuccess(r)));
                            zzzx.zzayN.set(Boolean.valueOf(false));
                            this.zzaBU.zzd(r);
                            googleApiClient = (GoogleApiClient) this.zzaBU.zzayQ.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zzaBU);
                            }
                        } catch (RuntimeException e) {
                            this.zzaBU.zzaBR.sendMessage(this.zzaBU.zzaBR.obtainMessage(1, e));
                            zzzx.zzayN.set(Boolean.valueOf(false));
                            this.zzaBU.zzd(r);
                            googleApiClient = (GoogleApiClient) this.zzaBU.zzayQ.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zzaBU);
                            }
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            zzzx.zzayN.set(Boolean.valueOf(false));
                            this.zzaBU.zzd(r);
                            googleApiClient = (GoogleApiClient) this.zzaBU.zzayQ.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zzaBU);
                            }
                        }
                    }
                });
            } else if (zzwv()) {
                this.zzaBO.onSuccess(r);
            }
        }
    }

    @NonNull
    public <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult com_google_android_gms_internal_zzabp;
        boolean z = true;
        synchronized (this.zzayO) {
            zzac.zza(this.zzaBM == null, (Object) "Cannot call then() twice.");
            if (this.zzaBO != null) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.zzaBM = resultTransform;
            com_google_android_gms_internal_zzabp = new zzabp(this.zzayQ);
            this.zzaBN = com_google_android_gms_internal_zzabp;
            zzwt();
        }
        return com_google_android_gms_internal_zzabp;
    }

    public void zza(PendingResult<?> pendingResult) {
        synchronized (this.zzayO) {
            this.zzaBP = pendingResult;
            zzwt();
        }
    }

    void zzwu() {
        this.zzaBO = null;
    }
}
